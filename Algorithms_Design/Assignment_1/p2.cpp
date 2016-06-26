#include <iostream>
#include <fstream>
#include <map>
#include <vector>
#include <cstring>

using namespace std;

ifstream evalin ("evaluare.in");
ofstream evalout ("evaluare.out");	

int lookup[300][1300];

#define MIN(a, b) ((a < b) ? a : b)
#define min(a, b, c) ((MIN(a, b) < c) ? MIN(a, b) : c)

//o functie care determina efectiv dinstanta minima de la 
//vectorii-varianta la vectorul initial
int levenshtein (int initial[], int initialSize, int lengthVar)
{
	int c[lengthVar + 1][initialSize + 1];
	for (int i = 1; i <= lengthVar; i++)
		c[i][0] = i;
	for (int j = 1; j <= initialSize; ++j)
		c[0][j] = j;
	c[0][0] = 0;
	for (int j = 1; j <= initialSize; j++)
	{
		for (int i = 1; i <= lengthVar; ++i)
		{
			if (lookup[initial[j - 1]][i - 1]==1)
			{
				c[i][j] = min(c[i][j - 1] + 1, c[i - 1][j] + 1, c[i - 1][j - 1]);
			}
			else
				c[i][j] = min(c[i][j - 1] + 1, c[i - 1][j] + 1,
									c[i - 1][j - 1] + 1);
		}
	}
	return c[lengthVar][initialSize];
}
int main() 
{
	int nr_var, lung_variante;
	int lung_initial;
	evalin >> nr_var >> lung_variante;
	//construiesc matricea de lookup, pentru a avea timp
	//O(1) cand fac un query de tipul: "este la indexul i
	//cifra x pe oricare din vectorii-varianta?"
	for (int i = 0; i < nr_var; ++i)
	{
		for (int j = 0; j < lung_variante; ++j)
		{
			int x;
			evalin >> x;
			lookup[x][j] = 1;
		}
	}
	evalin >> lung_initial;
	int initial[lung_initial + 1];

	for (int i = 0; i < lung_initial; ++i)
	{
		int x;
		evalin >> x;
		initial[i] = x;
	}
	evalout << levenshtein (initial, lung_initial, lung_variante);
	return 0;
}