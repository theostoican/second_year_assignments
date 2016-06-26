#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> 
#include <iostream>
#include <string>
#include <arpa/inet.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>
#include <vector>


using namespace std;

#define BUFLEN 4096
#define HTTP_PORT 80
#define MAX_LEVEL 5

bool everything;
bool recursive;
int nr_bucket = -1;
FILE *log = stderr;
FILE *logOut = stdout;
vector<string> newLinks;

void error(const char *msg)
{
    perror(msg);
    exit(0);
}

int hostname_to_ip(const char * hostname , char* ip)
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

ssize_t Readline(int sockd, void *vptr, size_t maxlen) {
    ssize_t n, rc;
    char    c, *buffer;

    buffer = (char*)vptr;

    for ( n = 1; n < maxlen; n++ ) {	
        if ( (rc = read(sockd, &c, 1)) == 1 ) {
            *buffer++ = c;
            if ( c == '\n' )
            break;
        }
        else if ( rc == 0 ) {
            if ( n == 1 )
                return 0;
            else
            break;
        }
        else {
            if ( errno == EINTR )
            continue;
            return -1;
        }
    }
    *buffer = 0;
    return n;
}

void recursiveDownload (string path, string page, string url, 
	struct sockaddr_in& servaddr, int serverSocket)
{
	string wholePage;
	int sockfd;
	char buffer[BUFLEN];
	int n;

	sprintf(buffer, "%s", "listen");
	n = send (serverSocket, buffer, sizeof(buffer), 0);
	n = recv (serverSocket, buffer, sizeof(buffer), 0);
	if (strstr(buffer, "ACK") == NULL)
	{
		fprintf(log, "Not received ACK when sending !\n");
	}
	sprintf(buffer, "%s", path.c_str());
	n = send(serverSocket, buffer, sizeof(buffer), 0);
	n = recv(serverSocket, buffer, sizeof(buffer), 0);
		if (strstr(buffer, "ACK") == NULL)
	{
		fprintf(log, "Not received ACK when sending !\n");
	}
	sprintf(buffer,"%s", page.c_str());
	n = send(serverSocket, buffer, sizeof(buffer), 0);
	if ( (sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0)
	{
		fprintf(log, "Socket opening error !\n");
		exit(-1);
	}
	if (connect (sockfd, (struct sockaddr*) &servaddr, sizeof(servaddr)) < 0)
	{
		fprintf(log,"Connection error !\n");
		exit(-1);
	}

	sprintf (buffer, "GET %s%s HTTP/1.0\n\n", path.c_str(), page.c_str());
	write (sockfd, buffer, strlen(buffer));
	memset(buffer, 0, BUFLEN);
	int numBytes = Readline(sockfd, buffer, BUFLEN);
	if (strstr(buffer, "200") == NULL)
	{
		fprintf(log,"Error, not received 200 when trying to access a resource!\n");
	}

	buffer[0] = 1;
	numBytes = Readline(sockfd, buffer + 1, BUFLEN - 1);
	while (numBytes != 2)
	{		
		numBytes = Readline(sockfd, buffer + 1, BUFLEN - 1);
	}
	memset(buffer, 0, BUFLEN);
	buffer[0] = 1;
	char *buffer2 = (char*)malloc(4096*sizeof(char));
	buffer2[0] = 1;
	while ( (n = Readline(sockfd, buffer+1, BUFLEN-1)) )
	{
		sprintf (buffer2+1,"%d", n);
		n = send (serverSocket, buffer2, sizeof(buffer), 0);
		n = send (serverSocket, buffer, sizeof(buffer), 0);
		wholePage.append(buffer+1);
		memset (buffer, 0, BUFLEN);
		buffer[0] = 1;
	}
	close(sockfd);
	free(buffer2);
	buffer[0] = 0;
	n = send(serverSocket, buffer, sizeof(buffer), 0);
	char *temp = (char*) malloc(sizeof(char) *  wholePage.size());
	temp = strdup(wholePage.c_str());
	wholePage.clear();
	char *aux = strtok(temp, "\n");
	while(aux != NULL && (recursive == true || everything == true))
	{	
		if (recursive == true)
			if (strstr(aux, "http") == NULL && strstr(aux, "href=") != NULL && strstr(aux, ".htm") != NULL
				&& strstr(aux, "#") == NULL && strstr(aux, "\\") == NULL && (strstr(aux, "<a") != NULL ||
					strstr(aux, "</a") != NULL))
			{
				char *useful = strstr(aux, "href=");
				char c = useful[6];
				int i = 6;
				string temp2;
					while (c != '\'' && c != '\"') {
						temp2 += c;
						c = useful[++i];
					}
					newLinks.push_back(temp2);
			}
		if (everything == true)
		{
			if (strstr(aux, "http") == NULL && strstr(aux, "href=") != NULL 
				&& strstr(aux, ".htm") == NULL && strstr(aux, "mailto:") == NULL && 
				strstr (aux,".") != NULL && strstr(aux, "#") == NULL && (strstr(aux, "<a") != NULL 
					|| strstr(aux, "</a") != NULL))
			{
				char *useful = strstr(aux, "href=");
				char *useful2 = strstr(useful,".");
				if (useful2 == NULL )
				{
					aux = strtok (NULL, "\n");
					continue;
				}
				char c = useful[6];
				int i = 6;
				string temp2;
				while (c != '\'' && c != '\"') {
					temp2 += c; 
					c = useful[++i];
				}
				newLinks.push_back(temp2);
			}
		}
		aux = strtok (NULL, "\n");
	}
	if (recursive == true || everything == true)
	{
		buffer[0] = 1;
		sprintf(buffer+1, "%d", nr_bucket);
		n = send(serverSocket, buffer, sizeof(buffer), 0);
		buffer[0] = 1;
		bool valid = false;
		for (unsigned int i = 0; i < newLinks.size(); i++)
		{
			//se elimina cele private
			valid = false;
			std::size_t found = newLinks[i].find("private");
			if (found != std::string::npos)
			{
				continue;
			}
			if (everything == true && nr_bucket <= 4)
			{
				if (strstr (newLinks[i].c_str(), ".htm") == NULL)
				{
					if (newLinks[i][0] == '/')
					{
						if (recursive == true)
						{
							valid = true;
							sprintf (buffer+1, "%d download http://%s%s",nr_bucket,
							 url.c_str(), newLinks[i].c_str());
						}
						else
						{
							valid = true;
							sprintf (buffer+1, "%d download http://%s%s",nr_bucket, 
								url.c_str(), newLinks[i].c_str());

						}
					}
					else
					{
						if (recursive == true)
						{	
							valid = true;
							sprintf (buffer+1, "%d download http://%s%s%s", nr_bucket,
							 url.c_str(),path.c_str(), newLinks[i].c_str());
						}
						else
						{
							valid = true;
							sprintf (buffer+1, "%d download http://%s%s%s", nr_bucket,
							 url.c_str(),path.c_str(), newLinks[i].c_str());

						}
					}
				}
			}
			if (valid == false && recursive == true && nr_bucket + 1 <= 4)
			{
				if (strstr (newLinks[i].c_str(), ".htm") != NULL)
				{
					if (newLinks[i][0] == '/')
					{
						if (everything == true)
						{
							valid = true;
							sprintf (buffer+1, "%d download -r -e http://%s%s",
								nr_bucket + 1, url.c_str(), newLinks[i].c_str());
						}
						else
						{
							valid = true;
							sprintf (buffer+1, "%d download -r http://%s%s",
								nr_bucket + 1, url.c_str(), newLinks[i].c_str());
						}
					}
					else
					{
						if (everything == true)
						{
							valid = true;
							sprintf (buffer+1, "%d download -r -e http://%s%s%s",
								nr_bucket + 1, url.c_str(), path.c_str(), newLinks[i].c_str());
						}
						else
						{
							valid = true;
							sprintf (buffer+1, "%d download -r http://%s%s%s",
								nr_bucket + 1, url.c_str(), path.c_str(), newLinks[i].c_str());
						}
					}
				}
			}
			if (valid == false)
			{
				break;
			}
			n = send(serverSocket, buffer, sizeof(buffer), 0);
		}
	}
	buffer[0] = 0;
	n = send(serverSocket, buffer, sizeof(buffer), 0);
	n = recv(serverSocket, buffer, sizeof(buffer), 0);
	if (strstr(buffer, "ACK") == NULL)
	{
		fprintf(log, "Not received ACK from one packet !\n");
	}
	else
	{
		fprintf(logOut, "One file sent: success !\n");
	}
}
int main(int argc, char* argv[])
{
	string page;
	string path;
	string url;
	string ip_addr;
	string log_file;
	string log_err_file;
	int port;
	int port_http;
	char buffer[BUFLEN];
	int sockfd;
	//serv_addr pentru server
	//servaddr pentru server HTTP
    struct sockaddr_in servaddr, serv_addr;
    string link;

    everything = false;
    recursive = false;
    port_http = HTTP_PORT;
	if (argc < 5)
	{
		error ("Usage: ./client [-o <fisier_log>] -a <adresa ip server> -p <port>");
	}
	if (argc == 5)
	{
		for (unsigned int i = 1; i < argc; i++)
		{
			if (strcmp(argv[i], "-a") == 0)
				ip_addr.append(argv[i+1]);
			else if (strcmp(argv[i], "-p") == 0)
				port = atoi(argv[i+1]);
		}
	}
	else
	{
		for (unsigned int i = 1; i < argc; i++)
		{
			if (strcmp(argv[i], "-a") == 0)
				ip_addr.append(argv[i+1]);
			else if (strcmp(argv[i], "-p") == 0)
				port = atoi(argv[i+1]);
			else if (strcmp(argv[i], "-o") == 0)
				log_file.append(argv[i+1]);
		}
	}
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) 
        error("ERROR opening socket");
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(port);
    inet_aton(ip_addr.c_str(), &serv_addr.sin_addr);
    if (connect(sockfd,(struct sockaddr*) &serv_addr,sizeof(serv_addr)) < 0) 
        error("ERROR connecting");
    if (log_file.size() != 0)
	{
		log_file.append("_");
		sprintf(buffer, "%d",getpid());
		log_file.append(buffer);
		log_err_file.append(log_file);
		log_file.append(".stdout");
		log_err_file.append(".stderr");
		log = fopen(log_err_file.c_str(), "w");
		logOut = fopen(log_file.c_str(), "w");
	}  
	while (1)
	{
		recv(sockfd,buffer, sizeof(buffer),0);
		if (strstr(buffer, "download") != NULL)
		{
			char *aux = strtok (buffer, " ");
			nr_bucket = atoi(aux);
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
					link.append(aux);
				}
				aux = strtok (NULL, " ");
			}
			char *temp = strdup (link.c_str());
			aux = strtok (temp, "/");
			aux = strtok (NULL, "/");
			url.append(aux);
			aux = strtok (NULL, "/");
			path.append("/");
			while (aux != NULL)
			{
				if (strstr(aux, ".") != NULL)
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
			if (page.size() == 0)
				page.append("index.html");
			if ((page[0] == '.' || page[1] == '.') && page.size()<=2)
				page.append("/index.html");
			fprintf(logOut,"Descarc pagina %s de pe %s/%s\n",page.c_str(), 
				url.c_str(), path.c_str());
			fflush(logOut);

			struct hostent* host = gethostbyname(url.c_str());
			if (host == NULL)
			{
				perror("Host unknown");
			}
			struct in_addr ** l = (struct in_addr**) host->h_addr_list;
			memset( (char *)&servaddr,0,sizeof(servaddr));
			servaddr.sin_family = AF_INET;
			servaddr.sin_port = htons(port_http);
			memcpy((char*)&servaddr.sin_addr, l[0], host->h_length);

			recursiveDownload (path, page,url, servaddr, sockfd);

			//curat tot si pregatesc terenul pentru urmatoarea comanda
			path.clear();
			page.clear();
			link.clear();
			url.clear();
			newLinks.clear();
			recursive = false;
			everything = false;
		}
		else if (strstr (buffer, "exit") != NULL)
		{
			fprintf(logOut, "Exiting...\n");
			sprintf(buffer, "ACK");
			send (sockfd, buffer, sizeof(buffer), 0);
			break;
		}
	}	
	return 0;
}