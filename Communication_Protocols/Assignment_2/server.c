#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <map>
#include <string>
#include <iostream>
#include <fcntl.h>
#include <vector>
#include <sys/stat.h>
#include <dirent.h>
#include <algorithm>

using namespace std;

FILE *users_config;
FILE *shared_files;
#define MAX_LINE 100
#define BUFLEN 4097
#define MAX_CLIENTS	20

int main(int argc, char *argv[])
{
	int numOfUsers;
	int sizeLine;
	int numOfFiles;
	sizeLine = MAX_LINE;
    int sockfd, portno, newsockfd;
    unsigned int clilen;
    char buffer[BUFLEN];
    struct sockaddr_in serv_addr, cli_addr;
    vector<int> clienti(MAX_CLIENTS);
    vector<int> quitClients;
    int n;
    fd_set read_fds;	//multimea de citire folosita in select()
    fd_set tmp_fds;	//multime folosita temporar 
    int fdmax;		//valoare maxima file descriptor din multimea read_fds

	char *line;
	char *aux;
	vector<string> pair (2);
	line = (char*) malloc (sizeof(char) * MAX_LINE);
	if (argc < 4)
	{
		fprintf(stderr,"Usage : %s port users_config shared_files\n", argv[0]);
        exit(1);
	}
	users_config = fopen (argv[2], "r");
	shared_files = fopen (argv[3], "r");
	map <string, string> hashCredentials;
	multimap <string, string> hashFiles;
	map<int, std::pair<string,int> > downloadingFiles;
	int i = 0;
	int j;
	fgets(line, MAX_LINE, users_config);
	numOfUsers = atoi (line);
	while (i<numOfUsers){
		fgets (line, sizeLine, users_config);
		line[strlen(line)-1]='\0';
		j = 0;
		aux = strtok (line, " ");
		while (aux != NULL)
		{
			pair[j].assign(aux);
			j++;
			aux = strtok (NULL, " ");
		}
		hashCredentials.insert (std::pair<string, string>(pair[0], pair[1]));
		++i;
	}
	fgets(line, MAX_LINE, shared_files);
	numOfFiles = atoi (line);
	i = 0;
	while (i < numOfFiles)
	{
		fgets (line, sizeLine, shared_files);
		line[strlen(line)-1] = '\0';
		j = 0;
		aux = strtok (line, ":");
		while (aux != NULL)
		{
			pair[j].assign(aux);
			j++;
			aux = strtok(NULL, ":");
		}
		hashFiles.insert(std::make_pair<string,string> (pair[0],pair[1]));
		++i;
	}

    //Am terminat partea de citire din fisier.
    //Urmeaza partea de conexiune efectiva.
    
    //Golim multimea de descriptori de citire (read_fds) si multimea tmp_fds.   	
    FD_ZERO(&read_fds);
    FD_ZERO(&tmp_fds);

    sockfd = socket(AF_INET, SOCK_STREAM, 0);
     if (sockfd < 0) 
        perror("ERROR opening socket");
     
     portno = atoi(argv[1]);
	
     memset((char *) &serv_addr, 0, sizeof(serv_addr));
     serv_addr.sin_family = AF_INET;
     serv_addr.sin_addr.s_addr = INADDR_ANY;	// foloseste adresa IP a masinii
     serv_addr.sin_port = htons(portno);
     
     if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(struct sockaddr)) < 0) 
              perror("ERROR on binding");
     
     listen(sockfd, MAX_CLIENTS);

     //adaugam noul file descriptor (socketul pe care se asculta conexiuni) in multimea read_fds
     FD_SET (0, &read_fds);     
     FD_SET(sockfd, &read_fds);
     fdmax = sockfd;
     map <int, string> connectedUsers;
     map <int, int> filesToUpload;
     vector<string> transfer;
     int bruteForce = 0;
     bool quit = false;
     map<int, map<string, int> > uploadClients;
     while (1) {
		tmp_fds = read_fds; 
		//ma asigur daca trebuie sa inchid sau nu serverul
		//daca variabila quit e setata si nu am transferuri
		if (uploadClients.size() == 0 && quit)
		{
			
			for (int i = 0; i < clienti.size(); i++)
			{
				sprintf (buffer, "quit");
				n = send(clienti[i], buffer, sizeof(buffer), 0);
				n = recv (clienti[i], buffer, sizeof(buffer), 0);
				FD_CLR(clienti[i], &read_fds); 
			}
			close (sockfd);
			return 0;
		}
		if (select(fdmax + 1, &tmp_fds, NULL, NULL, NULL) == -1) 
			perror("ERROR in select");
		memset (buffer, 0, BUFLEN);
		for(i = 0; i <= fdmax; i++) {
			if (FD_ISSET(i, &tmp_fds)) {
				if (i == 0)
				{
					fgets (buffer, BUFLEN-1, stdin);
					buffer[strlen(buffer)-1]='\0';
					if (strstr (buffer, "quit") != NULL)
					{
						quit = true;
					}
				}
				else if (i == sockfd) {
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
						if (newsockfd > fdmax) { 
							fdmax = newsockfd;
						}
					}
					printf("Noua conexiune de la %s, port %d, socket_client %d\n ", inet_ntoa(cli_addr.sin_addr), ntohs(cli_addr.sin_port), newsockfd);
				}
				else if ((n = recv(i, buffer, sizeof(buffer), 0)) <= 0) {
					if (n == 0) {
						//conexiunea s-a inchis
						printf("selectserver: socket %d hung up\n", i);
					} 
					else {
						perror("ERROR in recv");
					}
					int toBeDeleted = false;
					vector<int>::iterator it;
					for (it = clienti.begin(); it != clienti.end(); ++it)
						if ((*it) == i)
						{
							toBeDeleted = true;
							break;
						}
					if (toBeDeleted)
						clienti.erase (it);
					close(i); 
					connectedUsers.erase(i);
					FD_CLR(i, &read_fds); // scoatem din multimea de citire socketul pe care 
				} 
				
				else { //recv intoarce >0
					map<int, map<string, int> >::iterator upload;
					upload = uploadClients.find (i);
					if (strstr (buffer, "upload") != NULL)
					{
						char* aux = strtok (buffer," ");
						aux = strtok (NULL, " ");
						string newFile (aux);
						string user = connectedUsers[i];
						sprintf (buffer, "%s/%s",user.c_str(), newFile.c_str());
						if (access (buffer, F_OK) != -1)
						{
							sprintf (buffer, "-9 : Fisier existent pe server");
							n = send (i, buffer, sizeof(buffer), 0);
						}
						else
						{
							int newFd = open(buffer, O_RDWR | O_CREAT | O_APPEND );
							string fullPath (buffer);
							transfer.push_back (newFile);
							uploadClients[i][fullPath] = newFd;
							filesToUpload[i] = newFd;
							sprintf (buffer, "ACK %s", fullPath.c_str());
							n = send (i, buffer, sizeof(buffer), 0);
						}							
					}
					else if (strstr (buffer, "delete") != NULL)
					{
						char *aux = strtok (buffer, " ");
						aux = strtok (NULL, " ");
						string fileName (aux);
						string user = connectedUsers[i];
						string fullpath;
						fullpath.append(user);
						fullpath.append("/");
						fullpath.append(fileName);
						if (access (fullpath.c_str(), F_OK) == -1)
						{
							sprintf (buffer, "-4 : Fisier inexistent");
							n = send (i, buffer, sizeof(buffer), 0);
						}
						else
						{
							if (std::find (transfer.begin(), transfer.end(), fileName) != transfer.end())
							{
								sprintf (buffer, "-10 File is busy");
								n = send (i, buffer, sizeof (buffer), 0);
							}
							else
							{
								unlink (fullpath.c_str());
								sprintf (buffer, "200 Fisier sters");
								n = send (i, buffer, sizeof(buffer), 0);
							}
						}
					}
					else if (strstr (buffer, "unshare") != NULL)
					{
						string currentUser = connectedUsers[i];

						char *aux = strtok (buffer, " ");
						aux = strtok (NULL, " ");
						string file (aux);

						string fullName;
						fullName.append(currentUser);
						fullName.append("/");
						fullName.append(aux);
						if (access (fullName.c_str(), F_OK) == -1)
						{
							sprintf (buffer, "-4 : Fisier inexistent");
							n = send (i, buffer, sizeof(buffer), 0);
						}
						else{
							std::pair<multimap<string, string>::iterator, 
									multimap<string, string>::iterator> ret;
							ret = hashFiles.equal_range(currentUser);
							bool fileIsShared = false;
							multimap<string, string>::iterator it;
				            for (it = 
									ret.first; it != ret.second; ++it)
				            {
				            	if (file == it->second)
				            	{
				            		fileIsShared = true;
				            		break;
				            	}
				            }
				            if (file == ret.second->second)
				            	fileIsShared = true, it = ret.second;
				            if (!fileIsShared)
				            {
				            	sprintf (buffer, "-7 Fisier deja privat");
				            	n = send (i, buffer, sizeof(buffer), 0);
				            }
				            else
				            {
				            	hashFiles.erase(it);
				            	sprintf (buffer, "200 Fisierul a fost setat ca private");
				            	n = send (i, buffer, sizeof(buffer), 0);
				            }
						}							
					}
					else if (strstr (buffer, "share") != NULL)
					{
						string currentUser = connectedUsers[i];

						char *aux = strtok (buffer, " ");
						aux = strtok (NULL, " ");
						string file (aux);

						string fullName;
						fullName.append(currentUser);
						fullName.append("/");
						fullName.append(aux);
						if (access (fullName.c_str(), F_OK) == -1)
						{
							sprintf (buffer, "-4 : Fisier inexistent");
							n = send (i, buffer, sizeof(buffer), 0);
						}
						else{
							std::pair<multimap<string, string>::iterator, 
									multimap<string, string>::iterator> ret;
							ret = hashFiles.equal_range(currentUser);
							bool fileIsShared = false;
				            for (multimap<string, string>::iterator it = 
									ret.first; it != ret.second; ++it)
				            {
				            	if (file == it->second)
				            	{
				            		fileIsShared = true;
				            		break;
				            	}
				            }
				            if (file == ret.second->second)
				            	fileIsShared = true;
				            if (fileIsShared)
				            {
				            	sprintf (buffer, "-6 Fisierul este deja partajat");
				            	n = send (i, buffer, sizeof(buffer), 0);
				            }
				            else
				            {
				            	hashFiles.insert(std::pair<string,string>(currentUser, file));
				            	sprintf (buffer, "200 Fisierul %s a fost partajat", file.c_str());
				            	n = send (i, buffer, sizeof(buffer), 0);
				            }
						}
					}						
					else if (strstr (buffer, "download") != NULL)
					{
						char *aux = strtok (buffer, " ");
						aux = strtok (NULL, " ");
						string user;
						string path;
						bool arond = false;
						if (strcmp (aux, "@") == 0)
						{
							user.append(connectedUsers[i]);
							arond = true;
						}
						else
							user.append(aux);
						path.append(user);
						aux = strtok (NULL, " ");
						path.append("/");
						path.append(aux);
						if (access (path.c_str(), F_OK) == -1)
						{
							sprintf (buffer, "-4 : Fisier inexistent");
							n = send (i, buffer, sizeof(buffer), 0);
						}
						else
						{
							string nameOfFile (aux);
							std::pair<multimap<string, string>::iterator, 
									multimap<string, string>::iterator> ret;
							ret = hashFiles.equal_range(user);
							bool fileIsShared = false;
				            for (multimap<string, string>::iterator it = 
									ret.first; it != ret.second; ++it)
				            {
				            	if (nameOfFile == it->second)
				            	{
				            		fileIsShared = true;
				            		break;
				            	}
				            }
				            if (ret.second->second == nameOfFile)
				            {
				            	fileIsShared = true;
				            }
				            if (!fileIsShared && !arond)
				            {
				            	sprintf (buffer, "-5 : Descarcare interzisa");
				            	n = send (i, buffer, sizeof(buffer), 0);
				            }								
				            else
				            {
				            	sprintf (buffer, "ACK");
				            	n = send (i, buffer, sizeof(buffer), 0);
				            	int newfd = open (path.c_str(), O_RDONLY);
				            	std::pair<string,int> value (nameOfFile, i);
				            	downloadingFiles[newfd] = value;
				            	FD_SET(newfd, &read_fds);
				            	if (newfd > fdmax)
				            		fdmax = newfd;
				            }
						}
					}		
					else if (strstr (buffer, "quit") != NULL)
					{
						quitClients.push_back(i);
					}										
					else if (strstr (buffer, "getfilelist") != NULL)
					{
						char *aux = strtok (buffer, " ");
						aux = strtok (NULL, " ");
						string temp(aux);
						string result;
						if (hashFiles.find(temp) == hashFiles.end())
						{
							sprintf(buffer, "-11 Utilizator inexistent");
							n = send(i, buffer, sizeof(buffer), 0);
							break;
						}
						std::pair<multimap<string, string>::iterator, 
								multimap<string, string>::iterator> ret;
						ret = hashFiles.equal_range(temp);

						DIR *dir;
					    struct dirent *dp;
					    char * file_name;
					    string file;
					    int size;
					    string temp3;
					    dir = opendir(aux);
					        while ((dp = readdir(dir)) != NULL) {
						        if ( !strcmp(dp->d_name, ".") || !strcmp(dp->d_name, "..") )
						        {
						            // do nothing 
						        } else {
						            file_name = dp->d_name; // use it
						            file.append(file_name);
						            bool shared = false;
						            struct stat st;
						            temp3.append(aux);
						            temp3.append("/");
						            temp3.append(file.c_str());
									stat(temp3.c_str(), &st);
									size = st.st_size;
									temp3.clear();
									result.append(file);
									result.append("\t");
									char* sizeString = (char*)malloc (100*sizeof(char));
									sprintf(sizeString, "%d bytes",size);
									
									result.append(sizeString);
									result.append("\t");
									free(sizeString);
									shared = false;
						            for (multimap<string, string>::iterator it = 
											ret.first; it != ret.second; ++it)
						            {
						            	if (file == it->second)
						            	{
						            		result.append("SHARED");
						            		result.append("\n");
						            		shared = true;
						            		break;
						            	}
						            }
						            if (ret.second->second == file)
						            {
						            	result.append("SHARED");
						            	result.append("\n");
						            	shared = true;
						            }
						            if (!shared)
						            {
						            	result.append("PRIVATE");
						            	result.append("\n");
						            }
						            file.clear();
						        }
						    }
						    closedir(dir);
						sprintf (buffer, "%s", result.c_str());
						n = send(i, buffer, sizeof(buffer), 0);
						result.clear();
					}
					else if (strstr (buffer, "getuserlist") != NULL)
					{
						map<string,string>::iterator it = hashCredentials.begin();
						string userList;
						if (it != hashCredentials.end())
						{
							do
							{
								userList.append(it->first);
								userList.append("\n");
								++it;
							} while(it != hashCredentials.end());
							userList = userList.substr(0,userList.size()-1);
						}
						sprintf(buffer, "%s", userList.c_str());
						n = send (i, buffer,sizeof(buffer), 0);
					}
					else if (strstr (buffer, "logout") != NULL)
					{
						if (connectedUsers.find(i) != connectedUsers.end())
						{
							connectedUsers.erase(i);
							sprintf (buffer, "finishLogout");
							n = send (i, buffer, sizeof(buffer), 0);
						}
					}
					else if (strstr(buffer, "login") != NULL)
					{
						vector<string> login (3);
						char* aux = strtok (buffer, " ");
						int m = 0;
						while (aux != NULL)
						{
							login[m].assign (aux);
							m++;
							aux = strtok (NULL, " ");
						}
						if ( !(hashCredentials.find(login[1]) == hashCredentials.end()) 
									&& hashCredentials[login[1]] == login[2])
						{
							if (connectedUsers.find (i) == connectedUsers.end()) // no user connected from this socket
							{ 
								connectedUsers[i] = login[1];
								sprintf (buffer, "%s authorized", login[1].c_str());
							}
							else 
							{
								sprintf (buffer, "-2 Sesiune deja deschisa");
							}
							n = send (i, buffer, sizeof(buffer), 0);
						}
						else 
						{
							++bruteForce;
							if (bruteForce == 3)
							{
								sprintf (buffer, "-8 Brute-force detectat");
								n = send (i, buffer, sizeof(buffer), 0);
								close(i);
								bruteForce = 0;
								int toBeDeleted = false;
								vector<int>::iterator it;
								for (it = clienti.begin(); it != clienti.end(); ++it)
									if ((*it) == i)
									{
										toBeDeleted = true;
										break;
									}
								if (toBeDeleted)
									clienti.erase (it);
							}
							else
							{
								sprintf (buffer, "-3 User/parola gresita");
								n = send (i, buffer, sizeof(buffer), 0);
							}
						}
					}
					//partea de upload, verific daca clientul curent mi-a trimis
					//o comanda tip upload
					else if (upload != uploadClients.end())
					{
						int whereToPrint = uploadClients[i][buffer];
						string fileName (buffer);
						n = recv (i, buffer, sizeof(buffer), 0);
						if (atoi(buffer) == 0)
						{
							uploadClients[i].erase(fileName);
							if (uploadClients[i].size() == 0)
							{
								uploadClients.erase(i);
							}
							transfer.erase(std::find (transfer.begin(), transfer.end(), fileName)); 
							close (whereToPrint);
							if (std::find (quitClients.begin(), quitClients.end(), i) != quitClients.end())
							{

								quitClients.erase(std::find(quitClients.begin(), quitClients.end(), i));
								int toBeDeleted = false;
								connectedUsers.erase(i);
								vector<int>::iterator it;
								for (it = clienti.begin(); it != clienti.end(); ++it)
									if ((*it) == i)
									{
										toBeDeleted = true;
										break;
									}
								if (toBeDeleted)
									clienti.erase (it);
								close(i); 
								FD_CLR(i, &read_fds);

							}
						}
						else
						{
							int numBytes = atoi (buffer);
							memset (buffer, 0, BUFLEN);
							n = recv (i, buffer, sizeof(buffer), 0);
							write (whereToPrint, buffer, numBytes);

						}
					}
				}
			} 
		}
	}
    close (sockfd);
	return 0;
}
