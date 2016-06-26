#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <map>
#include <cstring>
#include <cstdlib>
using namespace std;

ifstream jocin("joc.in");
ofstream jocout ("joc.out");

int lookup[257];

int findPair (char *cuvant, int pos, int letter, int imparePos)
{
	int newPos = -1;
	if (imparePos == -1)
		imparePos = 0;
	else
		imparePos = -1;
	//plec in cautarea perechei literei curente, ignorand pozitiile
	//anterior calculate
	for (int i = strlen(cuvant) - pos - 1 + imparePos; i < pos; ++i)
	{
		if (i != pos && cuvant[i] == letter)
		{
			newPos = i;
			return newPos;
		}
	}
	return newPos;
}

int findMinimumMoves (char* cuvant)
{
	int i;
	int totalMoves = 0;
	int middleWord;
	int imparePos = -1;
	int numberOfLettersInCentre = 0; //variabila pentru a determina 
	//daca un numar poate fi sau nu palindrom
	char aux;
	//cazul palindromului cu nr impar de litere
	if (strlen(cuvant) % 2 == 1)
	{	
		middleWord = strlen(cuvant)/2;
		int pos = strlen(cuvant) - 1;
		while (pos >= middleWord)
		{
			int secPos = findPair (cuvant, pos, cuvant[pos], imparePos);
			if (secPos != -1){ //nu e litera din centru
				int rightPos = middleWord - (pos - middleWord);
				if (imparePos != -1)
					rightPos = rightPos - 1;
				int iteratePos = secPos;
				while (iteratePos > rightPos)
				{
					//facem swap intre cele 2 litere si avansez mai departe
					aux = cuvant[iteratePos];
					cuvant[iteratePos] = cuvant[iteratePos - 1];
					cuvant[iteratePos - 1] = aux;
					iteratePos--;
				}
				totalMoves += secPos - rightPos; //intotdeauna pozitiv
			}
			else{ //e litera din centru
				//daca da, o mut in mijloc
				numberOfLettersInCentre++;
				if (numberOfLettersInCentre > 1)
					return -1;
				imparePos = pos;
			}
			--pos;
		}
		if (imparePos != -1)
		{
			int rightPos = middleWord;
			int iteratePos = imparePos;
			while (iteratePos > rightPos)
			{
				aux = cuvant[iteratePos];
				cuvant[iteratePos] = cuvant[iteratePos - 1];
				cuvant[iteratePos - 1] = aux;
				iteratePos--;
			}
			totalMoves += imparePos - rightPos;
		}
	}
	else //cazul par 
	{
		middleWord = strlen(cuvant)/2 - 1;
		int pos = strlen(cuvant) - 1;
		while (pos > middleWord)
		{
			int secPos = findPair (cuvant, pos, cuvant[pos], imparePos);
			if (secPos == -1)
				return -1;
			int rightPos = middleWord - (pos - middleWord) + 1;
			int iteratePos = secPos;
			while (iteratePos > rightPos)
			{
				aux = cuvant[iteratePos];
				cuvant[iteratePos] = cuvant[iteratePos - 1];
				cuvant[iteratePos - 1] = aux;
				--iteratePos;
			}
			totalMoves += secPos - rightPos; //intotdeauna pozitiv
			--pos;
		}
	}
	return totalMoves;
}

int main() {
	int n;

	jocin >> n;
	char *cuvinte = (char*) malloc (350 * sizeof(char));
	for (int i = 0; i <= n - 1; ++i)
	{
		jocin>>cuvinte;
		if (i != n-1)
			jocout << findMinimumMoves(cuvinte) << endl;
		else 
			jocout << findMinimumMoves(cuvinte);
	}
	return 0;
}
