#include <iostream>
#include <fstream>
#include <algorithm>
#include <vector>
#include <cstdio>
#include <climits>
#include <map>

using namespace std;

#define MAX_N 200000
#define MAX_LENGTH 20

ifstream kimin ("kim.in");
ofstream kimout ("kim.out");

vector<int> lev(MAX_N);
int T[MAX_LENGTH][MAX_N];
int muchii [MAX_LENGTH][MAX_N]; 

struct Edge
{
	int x;
	int y;
	int cost;
	int index;
	Edge (int x, int y, int cost, int index)
	{
		this->x = x;
		this->y = y;
		this->cost = cost;
		this->index = index;
	}
	bool operator==(const Edge& other)
	{
		return (other.x == this->x && other.y == this->y 
				&& other.cost == this->cost);
	}
};

//functie utila la sortarea muchiilor
bool compare (const pair<Edge,bool>& e1, const pair<Edge,bool>& e2)
{
	return e1.first.cost < e2.first.cost;
}

//In multimea de paduri disjuncte, gasesc parintele nodului x, 
//tinand cont de path compression
int find (vector<int>& p, int x)
{
	if(p[x] == x)
		return x;
	return (p[x] = find(p, p[x]));
}

vector <vector<std::pair<int,int> > > kruskall (vector <pair<Edge, 
	bool> >& realEdges,	int& n, long long& cost, vector<bool>& taken)
{
	cost = 0;
	vector <vector<std::pair<int,int> > >AMA(n+1);
	vector<pair<Edge, bool> > edges;
	for (unsigned int i = 0 ; i < realEdges.size(); ++i)
	{
		edges.push_back(realEdges[i]);
	}
	sort (edges.begin(), edges.end(), compare);
	vector<int> p (n+1);
	for (int i = 0; i < n; i++)
		p[i] = i;
	for (unsigned int i = 0; i < edges.size(); ++i)
	{
		int xroot = find (p, edges[i].first.x);
		int yroot = find (p, edges[i].first.y);
		//daca nu e ciclu
		if (xroot != yroot)
		{
			realEdges[edges[i].first.index].second = true;
			AMA[edges[i].first.x].push_back(make_pair(edges[i].first.y, 
						edges[i].first.cost));
			AMA[edges[i].first.y].push_back(make_pair(edges[i].first.x, 
						edges[i].first.cost));
			cost += edges[i].first.cost;
			//unesc cei 2 arbori
			p[xroot] = yroot;
		}
	}
	return AMA;
}

//dfs-ul calculeaza nivelul fiecarui nod, salveaza parintele
//si muchia de cost maxim dintre nodul curent si parinte
void dfs (vector<vector<std::pair<int,int> > >& ama, int node, int l, 
				vector<bool>& visited)
{
	lev[node] = l;
	visited[node] = true;
	for (unsigned int i = 0; i < ama[node].size(); ++i)
	{
		if (!visited[ama[node][i].first])
		{
			T[0][ama[node][i].first] = node;
			muchii[0][ama[node][i].first] = ama[node][i].second;
			dfs(ama, ama[node][i].first,l+1, visited);
		}
	}
}

//lca imi determina muchia de cost maxim dintre nodurile x si y:
//*calculeaza muchia de cost maxim dintre stramosul comun al lui
//x si y si x si dintre acelasi stramos si y
int lca (int x, int y)
{
	int costMax = INT_MIN;
	//fac swap daca y e mai "jos" decat x, intrucat
	//de-a lungul functiei eu operez pe x
	if (lev[x] < lev[y])
	{
		int aux = x;
		x = y;
		y = aux;
	}
	int log1, log2;
	//log1 si log reprezinta k-ul maxim, exponentul lui 2,
	//cati stramosi ma pot duce in sus de la nodul curent
	for (log1 = 1; (1<<log1) < lev[x]; ++log1);
	for (log2 = 1; (1<<log2) < lev[y]; ++log2);

	//il aduc pe x la nivelul lui y sau "deasupra" sa
	for (int k = log1; k >= 0; --k)
	{
		if (lev[x] - (1<<k) >= lev[y])
			costMax = max(costMax, muchii[k][x]), x=T[k][x];
	}
	//cazul cand y este stramosul lui x
	if (x==y){
		return costMax;
	}

	//"urc" si cu x si cu y deodata
	for (int k = log2; k >= 0; k--)
	{
		if (T[k][x] && T[k][x] != T[k][y])
			costMax = max (max (costMax, muchii[k][x]), muchii[k][y]), 
				x = T[k][x], y = T[k][y];
	}
	
	return max(max(costMax, muchii[0][x]), muchii[0][y]);
	
}

bool myfunction (int i,int j) { return (i<j); }

int main()
{
	int x,y,c;
	int n, m, q;
	kimin >> n;
	kimin >> m;
	kimin >> q;

	vector<pair<Edge, bool> > edges;
	vector<int> mandatoryEdges ;
	for (int i = 0; i < m; ++i)
	{
		kimin >> x >> y >> c;
		struct Edge e(x, y, c, i);
		edges.push_back(make_pair(e, false));
	}
	vector<bool> taken (edges.size(), false);
	for (int i = 0; i < q; ++i)
	{
		kimin >> x;
		mandatoryEdges.push_back(x);
	}
	long long cost = 0;
	vector<vector<std::pair<int,int> > >ama = kruskall(edges, n, cost, taken);
	kimout<<cost<<endl;
	
	vector<bool> visited(n+1, false);
	dfs(ama, 1, 0, visited);
	int k;
	//calculez al 2^k-lea stramos si muchia de cost maxim dintre un nod
	//si al 2^k-lea stramos al sau
    for(k = 1; (1 << k) <= n; ++k)
        for(int i = 1; i <= n; ++i)
            T[k][i] = T[k-1][T[k-1][i]], 
        	muchii[k][i] = max (muchii[k-1][T[k-1][i]], muchii[k-1][i]);
    for (unsigned int i = 0; i < mandatoryEdges.size(); ++i)
	{
		//nu mai fac o interogare pentru muchiile deja introduse in arbore
		if (edges[mandatoryEdges[i]-1].second == false) {
			Edge e = edges[mandatoryEdges[i]-1].first;			
			long long temp = lca(e.x, e.y);
			temp = cost - temp + e.cost;
			kimout<<temp<<endl;
		}
		else
		{
			kimout<<cost<<endl;
		}
	}
	return 0;
}