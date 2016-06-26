#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <cstdlib>
#include <cstring>
#include <cstdio>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/stat.h>


using namespace std;

#define BUFLEN 4096
#define MAX_CLIENTS	20
#define HTTP_PORT 80

vector<string> commands;
bool recursive = false;
int recursiveLevel = 0;
bool everything = false;
vector<int> buckets(5,0);
int currentClient = 0;
vector<bool> busyClients;
vector<std::pair<string, int> > IpAndPort;
int filledBuckets = false;
string linkDir;
FILE *log_out = stdout;
FILE *log_err = stderr;

int hostname_to_ip(char * hostname , char* ip)
{
    struct hostent *he;
    struct in_addr **addr_list;
    int i;
         
    if ( (he = gethostbyname( hostname ) ) == NULL) 
    {
        // get the host info
        herror("gethostbyname");
        return 1;
    }
 
    addr_list = (struct in_addr **) he->h_addr_list;
     
    for(i = 0; addr_list[i] != NULL; i++) 
    {
        //Return the first one;
        strcpy(ip , inet_ntoa(*addr_list[i]) );
        return 0;
    }
     
    return 1;
}

int main (int argc, char *argv[])
{
    int sockfd, portno, newsockfd;
    unsigned int clilen;
    char buffer[BUFLEN];
    struct sockaddr_in serv_addr, cli_addr;
    struct sockaddr_in servaddr;
    vector<int> clienti;
    fd_set read_fds;	//multimea de citire folosita in select()
    fd_set tmp_fds;	//multime folosita temporar 
    int fdmax;		//valoare maxima file descriptor din multimea read_fds
	int portClient = HTTP_PORT;
	int n;
	vector<string> linksToDownload;

	int port;
	bool recursive = false;
	bool everything = false;
	string logFile;
	vector<int> connectedClients;
	if (argc < 3)
	{
		printf ("Call error ! You should use this format: ./server [-r] [-e] [-o <fisier_log>] -p <port>");
		return 0;
	}
	if (argc == 3)
	{
		if (strcmp (argv[1], "-p") != 0)
		{
			printf ("Call error ! You should use this format: ./server [-r] [-e] [-o <fisier_log>] -p <port>");
			return 0;
		}
	}
	for (int i = 1; i < argc; i++)
	{
		if (strcmp(argv[i], "-p") == 0)
		{
			port = atoi (argv[i+1]);
		}
		else if (strcmp (argv[i], "-r") == 0)
		{
			recursive = true;
		}
		else if (strcmp (argv[i], "-o") == 0)
		{
			logFile.append(argv[i+1]);
		}
		else if (strcmp (argv[i], "-e") == 0)
		{
			everything = true;
		}
	}

	if (logFile.size() > 0)
	{
		string logErr;
		logErr.append(logFile);
		logFile.append(".stdout");
		logErr.append(".stderr");
		log_out = fopen(logFile.c_str(), "w");
		log_err = fopen(logErr.c_str(), "w");
		if (log_out == NULL)
		{
			return 1;
		}
	}
    //golim multimea de descriptori de citire (read_fds) si multimea tmp_fds 
    FD_ZERO(&read_fds);
    FD_ZERO(&tmp_fds);

    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) 
       perror("ERROR opening socket");
    portno = port;
	
    memset((char *) &serv_addr, 0, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;	// foloseste adresa IP a masinii
    serv_addr.sin_port = htons(portno);
    if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(struct sockaddr)) < 0) 
              perror("ERROR on binding");
     
    listen(sockfd, MAX_CLIENTS);
    //adaugam noul file descriptor (socketul pe care se asculta conexiuni) in multimea read_fds
    FD_SET(sockfd, &read_fds);
    fdmax = sockfd;
    FD_SET(0, &read_fds);


    //aici pentru client download http
	/* formarea adresei serverului */
	memset(&servaddr, 0, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_port = htons(portClient);
    
	while (1)
	{
		tmp_fds = read_fds; 
		int m;
		for (m = 0; m <= 4; m++)
			if (buckets[m] != 0)
			{
				break;	
			}
		if (m == 5 && filledBuckets == true)
		{
			std::vector<int>::iterator it;
			for (it = clienti.begin(); it != clienti.end(); ++it)
			{
				sprintf(buffer,"exit");
				n = send (*it, buffer, sizeof(buffer), 0);
				n = recv (*it, buffer, sizeof(buffer), 0);
				if (strcmp (buffer, "ACK") != 0)
				{
					fprintf(log_err, "Not received ACK from client %d when exit\n", *it);
				}
			}
			fprintf(log_out, "Serverul se inchide...\n");
			break;
		}
		else
		{
			int j = m;	
			int nr_sent = 0;
			if (clienti.size()>=5 && clienti.size() <= 10)
			{
				for (unsigned int i = 0; i < busyClients.size(); i++)
				{
					if (j > 4)
					{
						std::vector<int>::iterator it;
						for (it = clienti.begin(); it != clienti.end(); ++it)
						{
							sprintf(buffer,"exit");
							n = send (*it, buffer, sizeof(buffer), 0);
							n = recv (*it, buffer, sizeof(buffer), 0);
							if (strcmp (buffer, "ACK") != 0)
							{
								fprintf(log_err, "Not received ACK from client %d when exit\n", *it);
							}
						}
						fprintf(log_out, "Serverul se inchide...\n");
						return 0;
					}
					if (buckets[j] == 0)
						break;
					else if (busyClients[i] == false)
					{
						FILE *f;
						while (commands.size() > 0)
						{
							string urlLink;
							char *temp = strdup (commands[0].c_str());
							char *aux = strtok (temp, " ");
							aux = strtok (NULL, " ");
							aux = strtok (NULL, " ");
							while (aux != NULL)
							{
								if (strcmp(aux,"-e") == 0)
								{
									everything = true;
								}
								else if (strcmp(aux, "-r") == 0)
								{
									recursive = true;
								}
								else
								{
									urlLink.append(aux);
								}
								aux = strtok (NULL, " ");
							}
							temp = strdup (urlLink.c_str());
							aux = strtok (temp, "/");
							aux = strtok (NULL, "/");
							aux = strtok (NULL, "/");
							string path;
							string page;
							path.append("/");
							while (aux != NULL)
							{
								if (strstr(aux, ".htm") != NULL)
								{
									char c = aux[0];
									unsigned int j = 0;
									while (c != '\n' && j < strlen(aux))
									{
										page+=c;
										c = aux[++j];
									}
								}
								else
								{
									path.append(aux);
									path.append("/");
								}
								aux = strtok(NULL, "/");
							}
							sprintf(buffer, "%s%s", path.c_str()+1, page.c_str());
							f = fopen(buffer, "r");
							if (f == NULL)
							{
								fprintf(log_out, "Se descarca pagina (relativ la directorul curent): %s\n",
										buffer);
								sprintf (buffer, "%s", commands[0].c_str());
								n = send(clienti[i], buffer, sizeof(buffer), 0);
								nr_sent++;
								commands.erase(commands.begin());
								buckets[j]--;
								break;
							}
							else
							{
								commands.erase(commands.begin());
								buckets[j]--;
								if (buckets[j] == 0)
								{
									if (nr_sent > 0)
										break;
									else
									{
										
										i--;
										j++;
										break;
									}
								}
							}
						}
					}
				}
				int i;
				for (i = 0; i <=4 ; ++i)
				{
					if (buckets[i] != 0)
						break;
				}
				if (i == 5 && clienti.size()>0 && nr_sent == 0)
				{
					std::vector<int>::iterator it;
					for (it = clienti.begin(); it != clienti.end(); ++it)
					{
						sprintf(buffer,"exit");
						n = send (*it, buffer, sizeof(buffer), 0);
						n = recv (*it, buffer, sizeof(buffer), 0);
						if (strcmp (buffer, "ACK") != 0)
						{
							fprintf(log_err, "Not received ACK from client %d when exit\n", *it);
						}
					}
					fprintf(log_out, "Serverul se inchide...\n");
					return 0;
				}
			}
		}
		if (select(fdmax + 1, &tmp_fds, NULL, NULL, NULL) == -1) 
			perror("ERROR in select");
		for(int i = 0; i <= fdmax; i++) {
			if (FD_ISSET(i, &tmp_fds)) {
			
				if (i == sockfd) {
					// a venit ceva pe socketul inactiv(cel cu listen) = o noua conexiune
					// actiunea serverului: accept()
					clilen = sizeof(cli_addr);
					newsockfd = accept(sockfd, (struct sockaddr *)&cli_addr, &clilen);
					if (newsockfd == -1) {
						perror("ERROR in accept");
					} 
					else {
						//adaug noul socket intors de accept() la multimea descriptorilor de citire
						FD_SET(newsockfd, &read_fds);
						clienti.push_back(newsockfd);
						sprintf(buffer, "%d.%d.%d.%d",
							  int(cli_addr.sin_addr.s_addr&0xFF),
							  int((cli_addr.sin_addr.s_addr&0xFF00)>>8),
							  int((cli_addr.sin_addr.s_addr&0xFF0000)>>16),
							  int((cli_addr.sin_addr.s_addr&0xFF000000)>>24));
						string temp(buffer);
						IpAndPort.push_back(make_pair(temp, (int) ntohs(cli_addr.sin_port)));
						busyClients.push_back(false);
						if (newsockfd > fdmax) { 
							fdmax = newsockfd;
						}
					}
					fprintf(log_out,"Noua conexiune de la %s, port %d, socket_client %d\n ", 
						inet_ntoa(cli_addr.sin_addr), ntohs(cli_addr.sin_port), newsockfd);
				}
				else if (i == 0)
				{
					read(0, buffer, BUFLEN);
					if (strstr(buffer, "download") != NULL)
					{
						char **aux = (char**) malloc(3*sizeof(char));
						int cntr = 0;
						char *temp0 = strtok(buffer, " ");
						temp0 = strtok (NULL, " ");
						while (temp0 != NULL)
						{
							aux[cntr]=(char*)malloc(50*sizeof(char));
							aux[cntr] = strdup(temp0);
							temp0 = strtok(NULL, " ");
							cntr++;
						}
						cntr--;
						if (recursive == true && everything == true){
							linkDir.append(aux[0]);
							sprintf(buffer, "%d download -r -e %s",0, aux[0]);
						}
						else if (recursive == true || everything == true)
						{
							linkDir.append(aux[0]);
							if (recursive == true)
								sprintf(buffer, "%d download -r %s", 0, aux[0]);
							else if (everything == true)
								sprintf(buffer, "%d download -e %s", 0, aux[0]);
						}
						else if(cntr == 0)
						{
							linkDir.append(aux[0]);
							sprintf(buffer, "%d download %s", 0, aux[0]);
						}
						fprintf(log_out, "Se descarca pagina %s\n",aux[0]);
						char *temp4 = strdup(linkDir.c_str());
						temp4 = temp4 + 7;
						char * aux4 = strtok(temp4, "/");
						linkDir.clear();
						linkDir.append(aux4);
						string comm (buffer);
						commands.push_back(comm);
						buckets[0] += 1;
						filledBuckets = true;
						sprintf (buffer, "mkdir -p %s", linkDir.c_str());
						system (buffer);
						chdir(linkDir.c_str());
					}
					else if (strstr(buffer, "status") != NULL)
					{
						for (unsigned int i = 0; i < IpAndPort.size(); ++i)
						{
							fprintf(log_out,"%s %d\n", IpAndPort[i].first.c_str(), IpAndPort[i].second);
						}
					}
					else if (strstr(buffer, "exit") != NULL)
					{
						std::vector<int>::iterator it;
						for (it = clienti.begin(); it != clienti.end(); ++it)
						{
							sprintf(buffer,"exit");
							n = send (*it, buffer, sizeof(buffer), 0);
							n = recv (*it, buffer, sizeof(buffer), 0);
							if (strcmp (buffer, "ACK") != 0)
							{
								fprintf(log_err, "Not received ACK from client %d when exit\n", *it);
							}
						}
						fprintf(log_out, "Serverul se inchide...\n");
						return 0;
					}
				}
				else {
				// am primit date pe unul din socketii cu care vorbesc cu clientii
				//actiunea serverului: recv()
				memset(buffer, 0, BUFLEN);
					if ((n = recv(i, buffer, sizeof(buffer), 0)) <= 0) {
						if (n == 0) {
							//conexiunea s-a inchis
							printf("selectserver: socket %d hung up\n", i);
						} 
						else {
							perror("ERROR in recv");
						}
						int toBeDeleted = false;
						int counter = 0;
						vector<int>::iterator it;
						for (it = clienti.begin(); it != clienti.end(); ++it)
						{
							if ((*it) == i)
							{
								toBeDeleted = true;
								break;
							}
							++counter;
						}
						if (toBeDeleted){
							clienti.erase (it);
							busyClients.erase(busyClients.begin()+counter);
							IpAndPort.erase(IpAndPort.begin()+counter);
						}
						fprintf(log_out, "A iesit clientul de pe socket-ul %d\n", i);
						fflush(log_out);
						close(i); 
						FD_CLR(i, &read_fds); // scoatem din multimea de citire socketul pe care 
					}
					else
					{
						sprintf (buffer, "ACK");
						n = send (i, buffer, sizeof(buffer), 0);
						n = recv(i, buffer, sizeof(buffer), MSG_WAITALL);
						
						char *Aux = (char*)malloc(5000*sizeof(char));
						sprintf (Aux, "mkdir -p %s", buffer+1);
						if(Aux[strlen(Aux) - 1] == '\\')
							Aux[strlen(Aux) - 1] = '\0';
						struct stat s;
						if (!(stat (buffer, &s) == 0 && S_ISDIR(s.st_mode)))
						{
							system(Aux);
						}
						Aux = strdup(buffer+1);
						sprintf(buffer, "ACK");
						n = send(i, buffer, sizeof(buffer), 0);
						n = recv (i, buffer, sizeof(buffer), MSG_WAITALL);
						char* Aux2 = (char*)malloc(5000*sizeof(char));
						sprintf(Aux2, "%s%s", Aux, buffer);
						FILE *f = fopen(Aux2, "wb");
						if (f == NULL)
						{
							perror("Error when open file");
							return 1;
						}
						//prelucrare director
						memset (buffer, 0, sizeof(buffer));
						n = recv(i, buffer, sizeof(buffer), MSG_WAITALL);
						int numBytesReceived = atoi (buffer+1);
						n = recv(i, buffer, sizeof(buffer), MSG_WAITALL);
						while (1)
						{
							fwrite(buffer+1, numBytesReceived, 1, f);
							memset(buffer, 0, sizeof(buffer));
							n = recv (i, buffer, sizeof(buffer), MSG_WAITALL);
							if (buffer[0] == 0)
								break;
							numBytesReceived = atoi(buffer+1);
							n = recv (i, buffer, sizeof(buffer), MSG_WAITALL);
							if (buffer[0] == 0)
								break;
						}
						fclose(f);
						n = recv (i, buffer, sizeof(buffer), MSG_WAITALL);
						if (buffer[0] == 0)
						{
							//do nothing, transaction  finished
						}
						else
						{
							int nr_bucket = atoi(buffer+1);
							int counter = 0;
							n = recv(i, buffer, sizeof(buffer),MSG_WAITALL);
							while (buffer[0] == 1)
							{
								++counter;
								string temp(buffer+1);
								commands.push_back(temp);

								n = recv(i, buffer, sizeof(buffer),MSG_WAITALL);
							}
							if (counter != 0)
								buckets[nr_bucket] += counter;
						}
						sprintf(buffer, "ACK");
						n = send(i, buffer, sizeof(buffer), 0);
					} 	
				}
			}
		}	
	}
	close(sockfd);
	return 0;
}