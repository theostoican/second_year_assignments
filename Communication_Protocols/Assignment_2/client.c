#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> 
#include <unistd.h>
#include <arpa/inet.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <map>
#include <iostream>
#include <string>
using namespace std;

#define BUFLEN 4097


int PORT;

int main(int argc, char*argv[])
{
	int fileUpload = -1;
	int sockfd, n;
    struct sockaddr_in serv_addr;
    struct hostent *server;
    char buffer[BUFLEN];
    int waitForMessage = 0;

    fd_set read_fds;
    fd_set tmp_fds;
    int fdmax;
    int i;
    int connected = 0;
    int upload = 0;
    int download = 0;
    bool quit = false;
    char* connectedUser = NULL;
    string fileBuffer;
    int nr_messages = 0;
    int logPid = getpid();
    string fullLog;
    fullLog.append("client-");
    sprintf (buffer, "%d", logPid);
    fullLog.append(buffer);
    fullLog.append(".log");

    int logFd = open (fullLog.c_str(), O_CREAT | O_RDWR, 0777);
    map <int, string> filesToUpload;
    map <string, int> downloadingFiles;

    //golim multimea de descriptori de citire (read_fds) si multimea tmp_fds 
    FD_ZERO(&read_fds);
    FD_ZERO(&tmp_fds);

    if (argc < 3) {
    	fprintf(stderr,"Usage %s server_address server_port\n", argv[0]);
    	exit(0);
    }
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) 
        perror("ERROR opening socket");
    
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(atoi(argv[2]));
    inet_aton(argv[1], &serv_addr.sin_addr);
    
    if (connect(sockfd,(struct sockaddr*) &serv_addr,sizeof(serv_addr)) < 0) 
        perror("ERROR connecting");    
    FD_SET(0, &read_fds);
    FD_SET(sockfd, &read_fds);
    fdmax = sockfd;
        while(1){
        if (quit && !upload)
        {
        	write(logFd, fileBuffer.c_str(), fileBuffer.size());
			fileBuffer.clear();
        	return 0;
        }
    	else if (!connected && !waitForMessage) 
    	{
         	fileBuffer.append("$ ");
        	printf ("$ ");
        	fflush(stdout);   		
    	}
    	else if (connected && !waitForMessage && !upload && !download)
    	{
    		fileBuffer.append(connectedUser);
    		fileBuffer.append("> ");
    		printf("%s> ", connectedUser);
    		fflush(stdout);
    	}
    	tmp_fds = read_fds;
    	if (select(fdmax + 1, &tmp_fds, NULL, NULL, NULL) == -1) 
			perror("ERROR in select");


		memset (buffer, 0, BUFLEN);
		for (i = 0; i <= fdmax; i++)
		{
			if (FD_ISSET (i, &tmp_fds))
			{
				if (i == 0)
				{
			  		//citesc de la tastatura
                    		fgets(buffer, BUFLEN-1, stdin);
			    	buffer[strlen(buffer)-1] = '\0';
			    	if (strstr(buffer, "login") != NULL)
			    	{
			    		fileBuffer.append(buffer);
			    		fileBuffer.append("\n");
			    		if (!connected)
			    		{
			    			//trimit mesaj la server
			    			waitForMessage = 1;
					    	n = send(sockfd,buffer,sizeof(buffer), 0);
					    	if (n < 0) 
					        	 perror("ERROR writing to socket");
			    		}
			    		else 
			    		{
			    			fileBuffer.append("-2 Sesiune deja deschisa\n\n");
			    			write(logFd, fileBuffer.c_str(), fileBuffer.size());
			    			fileBuffer.clear();
			    			printf("-2 Sesiune deja deschisa\n");
			    		}
			    	}
			    	else if (strstr(buffer, "logout") != NULL)
			    	{
			    		fileBuffer.append(buffer);
			    		fileBuffer.append("\n");
			    		if (connected)	
			    		{
			    			//trimit mesaj la server
			    			connected = 0;
			    			waitForMessage = 1;
			    			free(connectedUser);
			    			connectedUser = NULL;
					    	n = send(sockfd,buffer,sizeof(buffer), 0);
					    	if (n < 0) 
					        	 perror("ERROR writing to socket");
					    }
					    else
					    {
					    	fileBuffer.append("-1 Clientul nu e autentificat\n\n");
			    			write(logFd, fileBuffer.c_str(), fileBuffer.size());
			    			fileBuffer.clear();
					    	printf("-1 Clientul nu e autentificat\n");
					    }
			    	}
			    	else if (strstr(buffer, "getuserlist") != NULL)
			    	{
			    		fileBuffer.append(buffer);
			    		fileBuffer.append("\n");
			    		if (!connected){
			    			fileBuffer.append("-1 Clientul nu e autentificat\n\n");
			    			write(logFd, fileBuffer.c_str(), fileBuffer.size());
			    			fileBuffer.clear();
			    			printf ("-1 Clientul nu e autentificat\n");
			    		}
			    		else
			    		{
			    			waitForMessage = 1;
			    			n = send(sockfd, buffer, sizeof(buffer), 0);
			    			if (n < 0) 
					        	 perror("ERROR writing to socket");
			    		}
			    	}
			    	else if (strstr(buffer, "getfilelist") != NULL)
			    	{
			    		fileBuffer.append(buffer);
			    		fileBuffer.append("\n");			    		
			    		if (!connected){
			    			fileBuffer.append("-1 Clientul nu e autentificat\n\n");
			    			write(logFd, fileBuffer.c_str(), fileBuffer.size());
			    			fileBuffer.clear();			    			
			    			printf ("-1 Clientul nu e autentificat\n");
			    		}
			    		else
			    		{
			    			waitForMessage = 1;
			    			n = send(sockfd, buffer, sizeof(buffer), 0);
			    			if (n < 0) 
					        	 perror("ERROR writing to socket");
			    		}
			    	}
			    	else if (strstr(buffer, "upload") != NULL)
			    	{
			    		fileBuffer.append(buffer);
			    		fileBuffer.append("\n");	
			    		char *temp = (char*)malloc (strlen(buffer));
			    		strcpy(temp, buffer);
			    		char *aux = strtok(temp, " ");
			    		aux = strtok (NULL, " ");
			    		if (access (aux, F_OK) == -1)
						{
							fileBuffer.append("-4 : Fisier inexistent\n\n");
			    			write(logFd, fileBuffer.c_str(), fileBuffer.size());
			    			fileBuffer.clear();			    							
							sprintf(buffer, "-4 : Fisier inexistent");
							printf("%s\n",buffer);
						}
						else
						{
				    		fileUpload = open (aux, O_RDONLY);
				    		upload++;
				    		if (fdmax < fileUpload)
				    		{
				    			fdmax = fileUpload; 
				    		}
				    		FD_SET(fileUpload, &read_fds);
				    		free(temp);
				    		n = send (sockfd, buffer, sizeof(buffer), 0);
				    		n = recv (sockfd, buffer, sizeof(buffer), 0);
				    		if (strstr(buffer, "ACK") == NULL)
				    		{
								fileBuffer.append(buffer);
								fileBuffer.append("\n\n");
				    			write(logFd, fileBuffer.c_str(), fileBuffer.size());
				    			fileBuffer.clear();			    			
					    		printf ("%s\n", buffer);
					    		FD_CLR(fileUpload, &read_fds);
					    		close (fileUpload);
					    		fileUpload = -1;
					    		upload--;
				    		}
				    		else
				    		{
				    			char *aux2 = strtok (buffer, " ");
				    			aux2 = strtok (NULL, " ");
				    			string temp2(aux2);
				    			filesToUpload[fileUpload] = temp2; 
				    			fileBuffer.append(connectedUser);
				    			fileBuffer.append("> ");
				    			printf("%s> ", connectedUser);
	    						fflush(stdout);
				    		}
				    	}
			    	}
			    	else if (strstr (buffer, "delete") != NULL)
			    	{
			    		fileBuffer.append(buffer);
			    		fileBuffer.append("\n");				    		
			    		n = send (sockfd, buffer, sizeof(buffer), 0);
			    		waitForMessage = 1;
			    	}
			    	else if (strstr (buffer, "unshare") != NULL)
			    	{
			    		fileBuffer.append(buffer);
			    		fileBuffer.append("\n");	
			    		n = send (sockfd, buffer, sizeof(buffer), 0);
			    		waitForMessage = 1;
			    	}			    	
			    	else if (strstr (buffer, "share") != NULL)
			    	{
			    		fileBuffer.append(buffer);
			    		fileBuffer.append("\n");			    		
			    		n = send (sockfd, buffer, sizeof(buffer), 0);
			    		waitForMessage = 1;
			    	}
			    	else if (strstr (buffer, "quit") != NULL)
			    	{
			    		fileBuffer.append(buffer);
			    		fileBuffer.append("\n");			    		
			    		n = send (sockfd, buffer, sizeof(buffer),0);
			    		quit = true;
			    		if (connectedUser != NULL)
			    		{
			    			printf("%s> ", connectedUser);
    						fflush(stdout);
    					}
			    	}
			    	else if (strstr (buffer, "download") != NULL)
			    	{
			    		char *copy = strdup (buffer);
			    		char *aux2 = strtok (copy, " ");
			    		aux2 = strtok (NULL, " ");
			    		aux2 = strtok (NULL, " ");
			    		int pid = getpid();

			    		n = send (sockfd, buffer, sizeof(buffer), 0);
			    		n = recv (sockfd, buffer, sizeof(buffer), 0);
			    		if (strstr (buffer, "ACK") == NULL)
			    		{
			    			printf ("%s\n", buffer);
			    		}
			    		else
			    		{
			    			string filename;
			    			sprintf (buffer, "%d", pid);
			    			filename.append(buffer);
			    			filename.append("_");
			    			filename.append(aux2);
			    			int newfd = open (filename.c_str(), O_CREAT | O_RDWR | O_APPEND);
			    			string key (aux2);
			    			downloadingFiles[aux2]= newfd;
			    			download = 1;
			    			printf("%s> ", connectedUser);
    						fflush(stdout);
			    		}
			    	}
			    }
			    else if (i == sockfd)
			    {
			    	n = recv(sockfd, buffer, sizeof(buffer), 0);
			    	if (strstr(buffer, "quit") != NULL)
			    	{
			    		write(logFd, fileBuffer.c_str(), fileBuffer.size());
			    		fileBuffer.clear();
			    		sprintf (buffer, "ACK");
			    		n = send (sockfd, buffer, sizeof(buffer), 0);
			    		return 0;
			    	}
			    	else if (strstr (buffer, "download") != NULL)
			    	{
			    		
			    		char *copy = strdup(buffer);
			    		char *nameOfFile = strtok (copy, " ");
			    		nameOfFile= strtok (NULL, " ");
			    		string strFile (nameOfFile);
			    		nameOfFile = strtok (NULL, " ");
			    		int size = atoi (nameOfFile);
			    		int currentFd = downloadingFiles[strFile];
			    		if (size == 0)
			    		{

			    			n = recv (sockfd, buffer, sizeof(buffer), 0);
			    			downloadingFiles.erase(strFile);
			    			close (currentFd);
			    			download = 0;
			    		}
			    		else
			    		{
			    			int numOfBytes = size;
			    			n = recv (sockfd, buffer, sizeof(buffer), 0);
			    			write (currentFd, buffer, numOfBytes);
			    		}
			    	}
			    	else if (strstr (buffer, "authorized") != NULL)
			    	{
			    		connected = 1;
			    		waitForMessage = 0;
			    		char *copyBuffer = strdup(buffer);
			    		char* aux = strtok(copyBuffer, " ");
			    		connectedUser = strdup (aux);
			    		fileBuffer.append("\n");
			    		write (logFd, fileBuffer.c_str(), fileBuffer.size());
			    		fileBuffer.clear();
			    	}
			    	else if (strstr(buffer, "finishLogout") != NULL)
			    	{
			    		connected = 0;
			    		waitForMessage = 0;
			    		fileBuffer.append("\n");
			    		write (logFd, fileBuffer.c_str(), fileBuffer.size());
			    		fileBuffer.clear();
			    	}
			    	else if (strstr(buffer, "Brute-force") != NULL) 
			    	{
			    		printf ("%s", buffer);
			    		fileBuffer.append(buffer);
			    		fileBuffer.append("\n");
			    		write (logFd, fileBuffer.c_str(), fileBuffer.size());
			    		fileBuffer.clear();			    		
			    		close(i);
			    		exit(1);
			    	}
			    	else if (strstr(buffer, "PRIVATE") != NULL ||
			    			strstr(buffer, "SHARED") != NULL )
			    	{
			    		waitForMessage = 0;
			    		fileBuffer.append(buffer);
			    		fileBuffer.append("\n");
			    		write (logFd, fileBuffer.c_str(), fileBuffer.size());
			    		fileBuffer.clear();				    		
			    		printf("%s", buffer);
			    		fflush(stdout);	
			    	}
			    	else
			    	{
			    		waitForMessage = 0;

			    		printf ("%s\n", buffer);
			    		fflush(stdout);
			    		fileBuffer.append(buffer);
			    		fileBuffer.append("\n\n");
			    		write (logFd, fileBuffer.c_str(), fileBuffer.size());
			    		fileBuffer.clear();			    		
			    		if (strstr (buffer, "-10 File is busy") != NULL)
			    		{
			    			printf("%s> ", connectedUser);
	    					fflush(stdout);
	    					fileBuffer.append(connectedUser);
	    					fileBuffer.append("> ");
			    		}
			    	}
			    	break;
			    }
			    //aici procesez partea de upload, verificand despre care
			    //file descriptor este vorba la momentul curent
			    for (map<int,string>::iterator it = filesToUpload
			    	.begin(); it != filesToUpload.end(); ++it)
			    {
				    if (i == it->first)
				    {
				    	int x;
				    	int dim1 = lseek (i, 0, SEEK_CUR);
				    	int dim2 = lseek (i, 0, SEEK_END);
				    	int numBytes = dim2 - dim1;
				    	lseek(i, dim1, SEEK_SET);
				    	if (numBytes > 0)
				    	{
				    		read(i, buffer, 4096);
				    		char *temp2 = (char*) malloc (BUFLEN*sizeof(char));
				    		if (numBytes > 4096)
				    			numBytes = 4096;
				    		memcpy (temp2, buffer, numBytes);
				    		sprintf (buffer, "%s", filesToUpload[i].c_str());
				    		n = send (sockfd, buffer, sizeof(buffer), 0);
				    		sprintf (buffer, "%d", numBytes);
				    		n = send (sockfd, buffer, sizeof(buffer), 0);
				    		memcpy (buffer, temp2, numBytes);	
				    		n = send (sockfd, buffer, sizeof(buffer), 0);
				    	}
				    	else
				    	{	char *aux = strdup(filesToUpload[i].c_str());
				    		aux = strtok (aux, "/");
				    		aux = strtok (NULL, "/");
				    		struct stat st;
				    		stat (aux, &st);
				    		int size = st.st_size;
				    		printf("Upload finished: %s - %d bytes\n", aux, size);
				    		fileBuffer.append ("Upload finished: ");
				    		fileBuffer.append (aux);
				    		sprintf (buffer, "%d", size);
				    		fileBuffer.append (" - ");
				    		fileBuffer.append (buffer);
				    		fileBuffer.append (" bytes\n\n");
			    			write (logFd, fileBuffer.c_str(), fileBuffer.size());
			    			fileBuffer.clear();			    					    		
				    		FD_CLR(i, &read_fds);
				    		close (i);
				    		fileUpload = -1;
				    		upload--;
				    		if (upload > 0)
				    		{
				    			fileBuffer.append(connectedUser);
				    			fileBuffer.append("> ");
				    			printf("%s> ", connectedUser);
	    						fflush(stdout);
				    		}
				    		sprintf (buffer, "%s", filesToUpload[i].c_str());
				    		n = send(sockfd, buffer, sizeof(buffer), 0);
				    		sprintf (buffer, "%d", 0); //finish- null message
				    		n = send (sockfd, buffer, sizeof(buffer), 0);
				    		filesToUpload.erase(i);
				    	}
				    	break;
				    }
				}
			}
		}
    }
    return 0;
}
