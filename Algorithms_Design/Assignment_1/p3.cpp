#include <iostream>
#include <vector>
#include <fstream>
#include <cstring>
#include <climits>
#include <cstdlib>
#include <string>
#include <algorithm>	
using namespace std;

ifstream cablin ("cablaj.in");
ofstream cablout ("cablaj.out");

struct elem {
	int height;
	int dist;
	//in acest vector pastrez pinii implicati pentru a reconstrui 
	//solutia la final
	vector<pair<int, int> > pins;
};

struct sort_pred {
    bool operator()(const std::pair<int,int> &left, 
    						const std::pair<int, int> &right) {
        return left.first < right.first;
    }
};

elem d[101][101];

//functie care gaseste minimul de cablari dintre x si y
void findMinimumInterval (vector<int>& pini, int x, int y)
{
	int min = INT_MAX;
	int sum;
	int height;
	int totalPins = INT_MIN;
	int pins;
	vector<pair<int, int> > p;

	for (int i = x; i <= y - 1; i++)
	{
		sum = d[x][i].dist + d[i + 1][y].dist;
		pins = (d[x][i].pins).size() * 2 + (d[i + 1][y].pins).size() * 2;
		if (pins > totalPins){
			min = sum;
			height = max(d[x][i].height, d[i + 1][y].height); 
			totalPins = pins;
			p.clear();
			p.insert(p.end(),d[x][i].pins.begin(),d[x][i].pins.end());
			p.insert(p.end(),d[i + 1][y].pins.begin(),d[i + 1][y].pins.end());
		}
		//cazul in care numarul de cablari este mai mic, iar numarul de pini
		//egal
		else if (pins == totalPins && sum < min){
			min = sum;
			height = max(d[x][i].height, d[i + 1][y].height);
			p.clear();
			p.insert(p.end(),d[x][i].pins.begin(),d[x][i].pins.end());
			p.insert(p.end(),d[i + 1][y].pins.begin(),d[i + 1][y].pins.end());
		}
	}
	//cazul in care putem avea cablu intre x si y si, daca da, este 
	//mai eficient?
	if ((pini[x] == 1 && pini[y] == 0) || (pini[x] == 0 && pini[y] == 1))
	{
		sum = d[x + 1][y - 1].dist + (d[x + 1][y - 1].height + 1) * 2 + y - x;
		pins = (d[x + 1][y - 1].pins).size() * 2 + 2;
		if (pins > totalPins || ((pins == totalPins) && (sum < min))){
			min = sum; 
			height = d[x + 1][y - 1].height + 1, totalPins = pins;
			p.clear();
			p.insert (p.end(),d[x + 1][y - 1].pins.begin(), 
								d[x + 1][y - 1].pins.end());
			p.push_back (pair<int, int>(x, y));
		}
	}	
	d[x][y].dist = min;
	d[x][y].height = height;
	d[x][y].pins = p;
}
int findMinimum (vector<int>& pini)
{
	unsigned int i, j, k;
	for (i = 0; i < pini.size(); ++i)
		memset(d[i], 0, sizeof(elem) * pini.size());
	//cazul de baza
	for (i = 0; i < pini.size() - 1; ++i)
	{
		if ((pini[i] == 0 && pini[i + 1] == 1) || (pini[i] == 1 && pini[i + 1] == 0))
		{
			d[i][i + 1].dist = 3,d[i][i + 1].height = 1; 
			d[i][i + 1].pins.push_back(pair<int,int>(i,i + 1));
		}
	}
	//plec din 2 in 2, apoi din 3 in 3 s.a.m.d.
	for (i = 2; i < pini.size(); i++)
	{
		for (j = 0, k = i; k < pini.size(); j++, k++)
		{
			findMinimumInterval (pini ,j, k);
		}
	}
	return d[0][pini.size() - 1].dist;
}


int main() {
	int n;
	cablin >> n;
	vector<int> pini (2 * n);
	char line[2 * n];
	cablin>>line;
	//transform inputul in cifre
	for (int i = 0; i <= 2 * n - 1; i++)
		pini[i] = line[i] - '0';
	cablout << findMinimum (pini) << endl;
	vector<pair<int, int> > p = d[0][pini.size() - 1].pins;
	sort(p.begin(), p.end(), sort_pred());
	for (unsigned int i = 0; i < p.size(); ++i)
	{
		cablout << p[i].first + 1 << " " << p[i].second + 1 << endl;
	}
	return 0;
}