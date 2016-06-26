#include <iostream>
#include <fstream>
#include <vector>

using namespace std;

ifstream fin ("portal.in");
ofstream fout ("portal.out");


void dfs (vector<vector<int> >& graph, vector<bool>& visited, int x, bool& cycle, 
						int parent)
{
	visited[x] = true;
	vector<int> neighbors = graph[x];
	for (unsigned int i = 0 ; i < neighbors.size(); ++i)
	{
		if (!visited[neighbors[i]])
		{
			dfs(graph, visited, neighbors[i], cycle, x);
		}
		//am gasit un ciclu si ma opresc, gasind un nod deja vizitat
		//care nu e parinte in cadrul unei componente conexe
		else if (neighbors[i] != parent)
		{
			cycle = true;
		}
	}
}

int main()
{
	int num_Isolated = 0;
	int n,m;
	int x,y;
	fin>>n>>m;
	vector<vector<int> >graph(n+1);
	vector<bool> visited(n+1, false);
	for (int i = 0; i < m; ++i)
	{
		fin >> x >> y;
		graph[x].push_back(y);
		graph[y].push_back(x);
	}
	bool cycle = false;
	//apelez dfs pentru fiecare nod (a.k.a determinand componente conexe)
	for (int i = 1; i <= n; ++i)
	{
		cycle = false;
		if (!visited[i]){
			dfs(graph, visited, i, cycle, -1);
			//cycle se activeaza cand gasesc ciclu intr-o CC, deci nu am noduri izolate
			//altfel, contorizez nr de noduri izolate
			if (cycle == false)
			{
				num_Isolated ++;
			}
		}
	}
	fout<<num_Isolated;
	return 0;
}