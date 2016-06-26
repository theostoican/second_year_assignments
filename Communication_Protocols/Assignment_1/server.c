#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>

#include "lib.h"

#define HOST "127.0.0.1"
#define PORT 10001

//functie care calculeaza bitul de paritate aferent unui numar
int parity (char c)
{
	unsigned char mask = 1 << 7;
	int i, result = 0;
	for (i = 0; i < 8; i++)
	{
		result ^= ((c&mask)!=0);
		mask>>=1;
	}
	return result;
}

//functie care verifica paritate dintr-un mesaj
int check_parity (msg r)
{
	int sum=0;
	int parity_bit;
	int mask = 1;
	int i;
	for (i = 1; i<r.len;i++)
	{
		sum^=parity(r.payload[i]);
	}
	parity_bit = mask & r.payload[0];
	return (parity_bit == sum);
}

//functie care construieste 2 octeti dintr-unul, folosind Hamming
char* construct_bytes(char x) 
{
  int v[8];   memset(v, 0, sizeof(v));
  
  int i, flag = 128;
  for(i = 0; i <= 7; i++, flag /= 2)
    if(flag & x)
      v[i] = 1;
    else
      v[i] = 0;
  char *ans = malloc(2 * sizeof(char));
  ans[0] = v[0]^v[1]^v[3]^v[4]^v[6];             ans[0] <<= 1; //p1 
  
  ans[0] += (v[0] + v[2] + v[3] + v[5] + v[6]) % 2;   ans[0] <<= 1; //p2
  ans[0] += v[0]; ans[0] <<= 1; //d1
  ans[0] += (v[1] + v[2] + v[3] + v[7]) % 2;    //p4
  ans[1] = v[1]; ans[1] <<= 1; //d2

  ans[1] += v[2]; ans[1] <<= 1; //d3
  ans[1] += v[3];ans[1] <<= 1;//d4
  ans[1] += (v[4] + v[5] + v[6] + v[7]) % 2;  ans[1] <<= 1;    //p8
  ans[1] += v[4]; ans[1] <<= 1; //d5
  ans[1] += v[5]; ans[1] <<= 1; //d6
  ans[1] += v[6]; ans[1] <<=1;//d7
  ans[1] += v[7]; //d8
  return ans;
}

//functie care transforma 2 octeti codificati folosind metoda Hamming, in
//octetul original
char transformAndCorrect (char* x)
{
	char syndrome=0;
	char initial_byte=0;
	char mask = 1;
	char p1=0, p2=0, p4=0, p8=0;
	//extragem bitii de paritate mai intai
	mask = 16;
	if ((mask & x[1]) != 0)
		p8 = 1;
	mask = 1;
	if (mask & x[0])
		p4 = 1;
	mask = 4;
	if (mask & x[0])
		p2 = 1;
	mask = 8;
	if (mask & x[0])
		p1 = 1;
	//calculam efectiv valorile lor, folosind si bitii de date
	p1 ^= ((((1<<1)&x[0])!=0) ^ (((1<<7)&x[1])!=0) ^ (((1<<5)&x[1])!=0) ^ (((1<<3)&x[1])!=0)
		^(((1<<1)&x[1])!=0));
	p2 ^= ((((1<<1)&x[0])!=0) ^ (((1<<6)&x[1])!=0) ^ (((1<<5)&x[1])!=0) ^ (((1<<2)&x[1])!=0) 
		^ (((1<<1)&x[1])!=0));
	p4 ^= ((((1<<7)&x[1])!=0) ^ (((1<<6)&x[1])!=0) ^ (((1<<5)&x[1])!=0)
		^ (((1<<0)&x[1])!=0));
	p8 ^= ((((1<<3)&x[1])!=0) ^ (((1<<2)&x[1])!=0) ^ (((1<<1)&x[1])!=0)
		^ (((1<<0)&x[1])!=0));
	syndrome = p8; syndrome <<=1;
	syndrome += p4; syndrome <<=1;
	syndrome += p2; syndrome <<=1;
	syndrome +=p1;

	initial_byte = 0;
	//constructia numarului 
	mask = 2; // se extrage d1
	if (mask & x[0])
		initial_byte += 1;
	initial_byte <<= 1;
	mask = 128; //se extrage d2
	if (mask & x[1])
		initial_byte+=1;
	initial_byte<<=1;
	mask = 64; //se extrage d3
	if (mask & x[1])
		initial_byte += 1;
	initial_byte <<=1;
	mask = 32; //se extrage d4
	if (mask & x[1])
		initial_byte +=1;
	initial_byte <<= 1;
	mask = 8; //se extrage d5
	if (mask & x[1])
		initial_byte += 1;
	initial_byte <<= 1;
	mask = 4; //se extrage d6
	if (mask & x[1])
		initial_byte += 1;
	initial_byte <<= 1;
	mask = 2; //se extrage d7
	if ( mask & x[1])
		initial_byte += 1;
	initial_byte <<= 1;
	mask = 1; //se extrage d8
	if (mask & x[1])
		initial_byte += 1;
	if (syndrome == 0)
		return initial_byte;
	else if (syndrome == 3) //aferent bitului d1
	{
		if ((128 & initial_byte) == 0)
			initial_byte |= 128; //il facem 1
		else 
			initial_byte &= 127; //il facem 0 
	}
	//masura de siguranta in cazul in care avem prea multe erori in mesaj, 
	// iar metoda Hamming nu poate corecta
	else if (syndrome > 12)
	{
		printf("Eroare, prea multe erori");
		return 1;
	}
	else if (syndrome >=5 && syndrome <= 7) //aferent bitilor d2-d4
	{
		if ((initial_byte & (1<<(11-syndrome))) == 0)
			initial_byte |= 1 << (11-syndrome);
		else
			initial_byte &= ((255 << (12-syndrome)) | (255 >> (syndrome - 3)));

	}
	else if (syndrome >= 9 && syndrome <= 12) //aferent bitilor d5-d8
	{
		if ((initial_byte & (1<<(12-syndrome))) == 0)
		{
			initial_byte |= 1 << (12-syndrome);
		}
		else 
			initial_byte &= ((255<<(13-syndrome)) | (255 >> (syndrome - 4)));
	}
	return initial_byte;
}

//functie care construieste un mesaj codificat folosind Hamming, plecand de la
//cel original
void construct_message (msg *r, msg *t)
{
	char* twoBytesIntoOne;
	int j, k=0;
	for (j = 0; j < (*r).len; ++j)
	{
		twoBytesIntoOne = construct_bytes((*r).payload[j]);
		(*t).payload[k] = twoBytesIntoOne[0];
		(*t).payload[k+1] = twoBytesIntoOne[1];
		k += 2;
	}
	(*t).len = k;
}

int main(int argc, char *argv[])
{
	msg r,t;
	int i, res, j, result = 0, k = 0;
	char result2;
	int left=0, right = 1000,number;
	char* twoBytesIntoOne = malloc(2*sizeof(char));
	char resultByte;
	printf("[RECEIVER] Starting.\n");
	init(HOST, PORT);

	//implementarea protocolului de transmitere cu metoda Hamming si corectie
	if (argc > 1 && strstr(argv[1], "hamming") != NULL)
	{
		for (i = 0; i < COUNT; ++i)
		{
			res = recv_message(&r);
			k = 0;
			//decodificarea mesajului
			for (j = 0; j < r.len; j+=2)
			{
				twoBytesIntoOne[0] = r.payload[j];
				twoBytesIntoOne[1] = r.payload[j+1];
				resultByte = transformAndCorrect(twoBytesIntoOne);
				r.payload[k] = resultByte;
				++k;
			}
			r.len = k;
			t.len = sprintf (t.payload,"ACK") + 1;

			res = send_message(&t);
			if (i == 0)
			{
				r.len = sprintf(r.payload,"Hello") + 1;
				construct_message(&r, &t);
				res = send_message(&t);
				res = recv_message(&r);
			}
			if (i == 3)	
			{
				r.len = sprintf (r.payload,"YEY");
				construct_message ( &r, &t);
				send_message (&t);
				recv_message (&r);
				r.len = sprintf(r.payload,"OK");
				k = 0;
				for (j = 0; j < r.len; ++j)
				{
					twoBytesIntoOne = construct_bytes(r.payload[j]);
					t.payload[k] = twoBytesIntoOne[0];
					t.payload[k+1] = twoBytesIntoOne[1];
					k += 2;
				}
				t.len = k;
				send_message(&t);
				recv_message(&r);
				continue;
			}
			if (i == 4)
			{
				number = (left+right)/2;
				r.len = sprintf(r.payload,"%d", number) + 1;
				construct_message(&r, &t);
				res = send_message(&t);
				recv_message(&r);
				continue;
			}
			if (i > 4) {
				if (strstr(r.payload,"bigger") != NULL) {
					left = number;
					number = (left+right)/2;
				}
				else if (strstr(r.payload,"smaller")!=NULL)
				{
					right=number;
					number = (left+right)/2;
				}
				if (strstr(r.payload,"success")==NULL){
					r.len = sprintf(r.payload,"%d",number) + 1;
					construct_message(&r, &t);
					res = send_message(&t);
					recv_message(&r);
				}
				//cazul de exit
				else 
				{
					res=recv_message(&r);
					t.len = sprintf(t.payload,"ACK") + 1;
					construct_message(&t, &r);
					send_message(&r);
					t.len = sprintf (t.payload, "exit") + 1;
					construct_message(&t, &r);
					send_message(&r);
					recv_message(&r);
					break;
				}
			}
		}
	}
	//Implementarea protocolului STOP and WAIT
	else if (argc > 1 && strstr(argv[1],"ack")!=NULL)
	{
		for (i = 0; i < COUNT; ++i)
		{
			res = recv_message(&r);
			t.len = sprintf(t.payload, "ACK");
			res = send_message(&t);
			if (i==0)
			{
				r.len = sprintf(r.payload, "Hello");
				res = send_message(&r);
				res = recv_message(&r);
				continue;
			}
			if (i == 3)	
			{
				r.len = sprintf (r.payload,"YEY") + 1;
				send_message (&r);
				recv_message (&r);

				r.len = sprintf(r.payload,"OK") + 1;
				send_message(&r);
				recv_message(&r);
				continue;
			}
			if (i == 4)
			{
				number = (left+right)/2;
				r.len = sprintf(r.payload,"%d", number) + 1;
				res = send_message(&r);
				recv_message(&r);
				continue;
			}
			if (i > 4) {
				if (strstr(r.payload,"bigger") != NULL) {
					left = number;
					number = (left+right)/2;
				}
				else if (strstr(r.payload,"smaller") != NULL)
				{
					right=number;
					number = (left+right)/2;
				}
				if (strstr(r.payload,"success") == NULL){
					r.len = sprintf(r.payload,"%d", number) + 1;
					res = send_message(&r);
					recv_message(&r);
				}
				//cazul de exit
				else 
				{
					res = recv_message(&r);
					t.len = sprintf(t.payload,"ACK") + 1;
					send_message(&t);
					t.len = sprintf (t.payload, "exit") + 1;
					send_message(&t);
					recv_message(&r);
					if (strstr(r.payload,"ACK") != NULL)
						break;
					else {
						printf("error");
						return 1;
					}
				}
			}
		}
	}
	//implementarea protocolului de verificare paritate si
	//retransmitere
	else if (argc > 1 && strstr(argv[1],"parity")!=NULL)
	{
		for (i = 0; i<COUNT; ++i)
		{
			res = recv_message(&r);

			//daca paritatea este in regula, se trimite ACK
			if (r.len == 4 || check_parity(r)){
				sprintf(t.payload,"ACK");
				result = 0;
				for (j = 0; j<=2;j++)
					result ^= parity(t.payload[j]);
				if (result == 0)
					result2=0, sprintf(t.payload, "%cACK",result2);
				if (result == 1)
					result2=1, sprintf(t.payload, "%cACK",result2);
				t.len = 4;
				send_message(&t);
			}
			//altfel, se cere retrimiterea mesajului
			else {

				sprintf(r.payload,"NACK");
				result = 0;
				for (j = 0; j<=3;j++)
					result ^= parity(r.payload[j]);
				if (result == 0)
					result2=0, sprintf(r.payload, "%cNACK",result2);
				if (result == 1)
					result2=1, sprintf(r.payload, "%cNACK",result2);
				r.len = 5;
				--i;
				send_message(&r);
				continue;
			}
			//se trimite hello
			if (i==0)
			{
				sprintf(r.payload,"Hello");
				result = 0;
				for (j = 0; j<=4;j++)
					result ^= parity(r.payload[j]);
				if (result ==1)
					result2 = 1, sprintf(r.payload, "%cHello",result2);
				else
					result2=0, sprintf(r.payload, "%cHello",result2);
				r.len = 7;
				res = send_message(&r);
				res = recv_message(&r);
				//cat timp primesc nack, retrimit mesajul
				while (r.len == 5){
					sprintf(r.payload,"Hello");
					result = 0;
					for (j = 0; j<=4;j++)
						result ^= parity(r.payload[j]);
					if (result ==1)
						result2 = 1, sprintf(r.payload, "%cHello",result2);
					else
						result2=0, sprintf(r.payload, "%cHello",result2);
					r.len = 7;
					res = send_message(&r);
					res = recv_message(&r);
				}
			}
			//trimit yey si ok
			//de fiecare data dupa ce trimit un mesaj, apelez si recv_message pentru a
			//primi confirmarea
			if (i==3)	
			{
				sprintf (r.payload,"YEY");
				result = 0;
				for (j = 0; j<=2;j++)
					result ^= parity(r.payload[j]);
				if (result ==1)
					result2 = 1, sprintf(r.payload, "%cYEY",result2);
				else
					result2=0, sprintf(r.payload, "%cYEY",result2);
				r.len = 5;
				send_message (&r);
				recv_message (&r);
				while (r.len == 5)
				{
					sprintf(r.payload, "%cYEY",result2);
					r.len = 5;
					send_message (&r);
					recv_message (&r);
				}
				sprintf(r.payload,"OK");
				result = 0;
				for (j = 0; j<=1;j++)
					result ^= parity(r.payload[j]);
				if (result ==1)
					result2 = 1, sprintf(r.payload, "%cOK",result2);
				else
					result2=0, sprintf(r.payload, "%cOK",result2);
				r.len = 4;
				send_message(&r);
				recv_message(&r);
				while (r.len == 5)
				{
					sprintf(r.payload, "%cOK",result2);
					r.len = 4;
					send_message (&r);
					recv_message (&r);
				}
				continue;
			}
			//trimit primul numar
			if (i == 4)
			{
				number = (left+right)/2;
				sprintf(r.payload,"%d", number);
				result = 0;
				for (j = 0; j<=3; ++j)
					result^= parity(r.payload[j]);
				if (result ==1)
					result2 = 1, r.len = 1 + sprintf(r.payload, "%c%d",result2,number);
				else
					result2=0, r.len = 1 + sprintf(r.payload, "%c%d",result2, number);
				res = send_message(&r);
				recv_message(&r);
				while (r.len == 5)
				{
					sprintf(r.payload, "%c%d",result2,number);
					r.len = 5;
					send_message (&r);
					recv_message (&r);
				}
				continue;
			}
			//tratez trimiterea celorlalte numere si cazurile success si exit
			if (i > 4) {
				//recalculez numerele in functie de mesajul primit
				if (strstr(r.payload+1,"bigger") != NULL) {
					left = number;
					number = (left+right)/2;
				}
				else if (strstr(r.payload+1,"smaller") != NULL)
				{
					right=number;
					number = (left+right)/2;
				}
				if (strstr(r.payload+1,"success")==NULL){
					sprintf(r.payload,"%d",number);
					result = 0;
					for (j = 0; j<=3; ++j)
						result^= parity(r.payload[j]);
					if (result ==1)
						result2 = 1, sprintf(r.payload, "%c%d",result2,number);
					else
						result2=0, sprintf(r.payload, "%c%d",result2, number);
					r.len = 5;
					res = send_message(&r);
					recv_message(&r);

					while (r.len == 5)
					{
						sprintf(r.payload, "%c%d",result2,number);
						r.len = 5;
						send_message (&r);
						recv_message (&r);
					}
				}
				else 
				{
					res=recv_message(&r);
					while (!(check_parity(r))) {
						sprintf(r.payload,"NACK");
						result = 0;
						for (j = 0; j<=3;j++)
							result ^= parity(r.payload[j]);
						sprintf(r.payload, "%cNACK",result);
						r.len = 5;
						--i;
						send_message(&r);
						recv_message(&r);
					}
					sprintf(t.payload,"ACK");
					result = 0;
					for (j = 0; j<=2;j++)
						result ^= parity(t.payload[j]);
					if (result == 0)
						result2=0, sprintf(t.payload, "%cACK",result2);
					else 
						result2=1, sprintf(t.payload, "%cACK",result2);
					t.len = 4;
					send_message(&t);

					sprintf(r.payload, "exit");
					result = 0;
					for (j = 0; j<=3; ++j)
						result^= parity(r.payload[j]);
					if (result ==1)
						result2 = 1, sprintf(r.payload, "%cexit",result2);
					else
						result2=0, sprintf(r.payload, "%cexit",result2);
					r.len = 6;
					res = send_message(&r);
					res = recv_message(&r);
					while (!(r.len ==4))
					{
						sprintf(r.payload, "exit");
						result = 0;
						for (j = 0; j<=3; ++j)
							result^= parity(r.payload[j]);
						if (result ==1)
							result2 = 1, sprintf(r.payload, "%cexit",result2);
						else
							result2=0, sprintf(r.payload, "%cexit",result2);
						r.len = 6;
						res = send_message(&r);
						res = recv_message(&r);
					}
					if (r.len == 4)
						break;
					else {
						printf("error");
						return 1;
					}
				}
			}
		}
	}
	//implementarea protocolului simplu
	else
	{
		for (i = 0; i < COUNT; i++) 
		{
			/* wait for message */
			res = recv_message(&r);
			if (res < 0) {
				perror("[RECEIVER] Receive error. Exiting.\n");
				printf("error");
				return -1;
			}
			/* send dummy ACK */
			if (i == 0)
			{
				r.len = sprintf(r.payload,"Hello");
				res = send_message(&r);
				continue;
			}
			if (i == 3){
				r.len = sprintf(r.payload,"%s","YEY");
				res = send_message(&r);
				
				r.len = sprintf(r.payload,"%s","OK");
				res = send_message(&r);
				continue;
			}
			if (i == 4) {
				number = (left+right)/2;
				r.len = sprintf(r.payload,"%d", number) + 1;
				res = send_message(&r);
				continue;
			}
			if (i > 4) {

				if (strstr(r.payload,"bigger")!=NULL) {
					left = number;
					number = (left+right)/2;
				}
				else if (strstr(r.payload,"smaller")!=NULL){
					right=number;
					number = (left+right)/2;
				}
				if (strstr(r.payload,"success")==NULL){
					r.len = sprintf(r.payload,"%d",number) + 1;
					res=send_message(&r);
				}
				else {
					res=recv_message(&r);
				}
			}
			if (strstr(r.payload,"job")!=NULL){
				r.len = sprintf(r.payload,"exit")+1;
				send_message(&r);
				break;
			}
			if (res < 0) {
				perror("[RECEIVER] Send ACK error. Exiting.\n");
				return -1;
			}
		}
	}

	printf("[RECEIVER] Finished receiving..\n");
	return 0;
}
