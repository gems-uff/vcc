/*
This is the WAP algorithm program based on the description in: 
Jian Pei, Jiawei Han, Behzad Mortazavi-asl, and Hua Zhu, "Mining Access 
Patterns Eciently from Web Logs", PAKDD 2000.

1.Development environment:
Although initial version is developed under the
hardware/software environment specified below, the program
runs on more powerful and faster multiprocessor UNIX environments as well.
	(1)Hardware: Intel Celeron 400 PC, 64M Memory;
	(2)Operting system: Windows 98;
	(3)Development tool: Inprise(Borland) C++ Builder 6.0.
Note: The algorithm is developed under C++ Builder 6.0. However, it is possible 
to compile the program under any standard C++ development tools and 
run it.
Program is in WAPtree.cpp, compiled with Unix "g++ WAPtree.cpp" and 
executed with a.out.


2. INPUT: 
(1) test.data:
For simplifying input process of the program, we assume that all input data 
has been preprocessed such that all events belongs to a same user id have been 
gathered together, and formed as a sequence which is saved in a named "test.data". 
The "test.data" file is composed of hundreds of thousands lines of sequence 
where each line represents a web access sequence for each user.

Every line include UserID, length of sequence and the sequence which are 
seperated by table spaces.
For example, given a line: 
100	5	10	20	40	10	30  
100 represents UserID, 5 means the length of sequence is 5, the sequence is
10,20,40,10,30. 

(2) middle.data:
used to save the conditoinal middle pattern. During the mining process of 
WAP tree, following the linkage, once the sum of support for a event is 
found greater than minimum support, all its prefix condition patterns are 
saved in the "middle.data" file for next round mining. The format of 
"middle.data" is following:
each line include the length of sequence, the occurrence of the sequence, 
and the events in the sequence.
For example, given a line in middle.data: 
5	4	10	20	40	10	30  
5 means the length of sequence is 5, 4 indicates the sequence occurred 4 
times in the previous condition WAP tree. the sequence is 10,20,40,10,30. 
			
	(3) minimum support:
The program also needs to accept a value between 0 and 1 which is called 
minimum support. 
The minimum support is prompted to enter when the program starts.

3. OUTPUT: result_WAP.data
Once the program terminates, we can find the result patterns in a file named 
"result_WAP.data".
It may contains lines of patterns. Each line represents a pattern.
4. METHODS:
	(1)BuildTree: Build the WAP tree/conditional WAP tree
	(2)MiningProcess: produce sequential pattern/conditional prefix sub-pattern from WAP tree/conditional WAP tree.

5. DATA STRUCTURE
	three struct are used in this program:
	(1) the node struct indicates a WAP node which contains the information:
		a.the event name
		b.the number of occurrence of the event
		e. the linkage to next node same event name in WAP tree
		d. a pointer to it's left son
		e. a pointer to it's rights sibling
		f. a pointer to it's parent

	(2) a linkage struct
		descibed in the program .

6. ADDTIONAL INFORMATION: 
The run time is display on the screen with start time, end time and total 
seconds for running the program.
*/

#include <iostream>
#include <map>
#include <fstream>
#include <list>
#include <stack>
#include <time.h>

using namespace std;

struct node
{
  	int event;              // This is structure for WAP tree.
  	int occur;              // The node in tree is composed of event
  	node *nextLink;         // and its occurrence. We use lSon to
  	node *lSon;             // indicate its first child, and rSibling
  	node *rSibling;         // to indicate its first sibling. The parent
  	node *parent;           // is the pointer to its actual parent in tree.
};

struct linkheader               
{                               //Structure of linkage header:
        int event;              //event name of the linkage
        node *link;             //link to the first occurrence of the event in tree
        node *lastLink;         //link to the last occurrence of the event in tree
};

typedef multimap<int, int, less<int> > sequence; // used to store the sequence read from the input file
int frequency;
float minSupp;
int runTime = 0;

void printtree(node*);					// For testing: print the whole WAP tree 
void printLinkage (list<linkheader>);			// For testing: print the linkage
void BuildTree(char*, stack<int>);			// Build the WAP tree
void MiningProcess(node*, list<linkheader>, stack<int>);// Mine sequential pattern from WAP tree

int main()
{
      stack<int> bPattern;

      cout << "please enter the frequency:";
      cin >> minSupp;
      //minSupp = 0.0005;

      ofstream result("result_WAP.data", ios::trunc);
      result.close();

      clock_t start, end;
      double cpu_time_used;


      int tim1 = time(0);
	start =clock();
      cout<<"Now start the program......\n\n"<<endl;
      BuildTree("test.data", bPattern);
	
      int tim2 = time(0);
      end = clock();
      cpu_time_used = ((double) (end - start)) / CLOCKS_PER_SEC;
        
      cout<<"begin time: "<<tim1<<'\n';
      cout<<"end time  : "<<tim2<<'\n';
      cout<<"The execution time is: "<< tim2-tim1 << endl;
      cout<<"The CPU time used is : "<< cpu_time_used << endl;        
      cout<<"\n\nEnd the program"<<endl;

      //ofstream WAPtreeExecuteTime("WAPtreeExecuteTime.txt", ios::app);
      //WAPtreeExecuteTime<<"\n\nRunning Result at Minimum Support at "<<minSupp;
      //WAPtreeExecuteTime<<"\n\nbegin time: "<<tim1<<'\n';
      //WAPtreeExecuteTime<<"end time  : "<<tim2<<'\n';
      //WAPtreeExecuteTime<<"The execution time is:\n"<< tim2-tim1;
      //WAPtreeExecuteTime<<"\n\nEnd the program"<<endl;
      //WAPtreeExecuteTime.close();

      return 0;
}

void printtree(node *start)
{
// For testing: print the whole WAP tree 
        if (start->lSon !=NULL)
        {
                cout<<"event:"<<start->event<<" occurrence = "<< start->occur<<".  the son is "<<(start->lSon)->event;
                if(start->event != -1) cout<<".... its parent is "<<(start->parent)->event;
                cout<<endl;
                printtree(start->lSon);
        }

        if (start->rSibling != NULL)
        {
                cout<<"event:"<<start->event<<" the sibling is "<< (start->rSibling)->event <<";  the occrrence are "<<(start->rSibling)->occur<<endl;
                printtree(start->rSibling);
        }
}

void printLinkage(list<linkheader> table)
{
// For testing: print the linkage

        list<linkheader>::iterator pnt = table.begin();
        while (pnt != table.end())
        {
                cout<<pnt->event;
                node *tr= pnt->link;
                while (tr !=NULL)
                {
	                cout<<"-->"<<tr->event<<"("<<tr->occur<<")";
	                tr=tr->nextLink;
                }
		    cout<<endl;
		    pnt++;
	  }

}



void MiningProcess(node *start, list<linkheader> lnkhdr, stack<int> basePattern)
{
/*
MiningProcess is the function for finding the pattern from WAP tree
This is a recurisive function combined with BuildTree fuction. 
Called in Parameters:
	(1) start: the root node of the conditional WAP tree.
	(2) basePattern: is the subsequence which is obtained in previous round
	(3) lnkhdr: the linkage list for the conditional WAP tree
		*/
        if ( start->lSon == NULL ||( (start->lSon)->lSon == NULL && (start->lSon)-> rSibling == NULL ))
        {
	// reach to a single branch or leaf node, and ready to output the result
                ofstream result("result_WAP.data", ios::app);
                if(start->lSon != NULL)
                {
                        result<< (start->lSon)->event;
                        while( !basePattern.empty())
                        {
                                result << "\t" << basePattern.top();
                                basePattern.pop();
                        }
                        result<<endl;
                }

                result.close();
                return;
       }
       else
       {
// there are multiple branchs exist, going to export the middle result and build the conditional WAP tree
            for(list<linkheader>::reverse_iterator i = lnkhdr.rbegin(); i != lnkhdr.rend(); i++)
            {
	// for each link header, going to produce the conditional prefix pattern
                ofstream middle("middle.data",ios::trunc);
                node * linkTranversal = i->link;
                bool firstTime = true;

                while( linkTranversal != NULL)
                {
                       node *patternBrow;
                       stack<int> pattern, subPattern;
                       int subCount, pattLength, subLength;
                       bool subStart = false;
                       patternBrow = linkTranversal;
                       pattLength = 0;
                       patternBrow = patternBrow->parent;

                       while( patternBrow != start)
                       {
                                pattern.push(patternBrow->event);
                                pattLength++;
                                if(!subStart)
                                {
                                        if(patternBrow->event == linkTranversal->event)
                                        {
                                                subStart = true;
                                                subLength = 0;
                                                subCount = linkTranversal->occur * (-1);
                                        }
                                }
                                else
                                {
                                        subPattern.push(patternBrow->event);
                                        subLength ++;
                                }
                                patternBrow = patternBrow->parent;
                       }

                       if (pattLength != 0)
                       {
	// output the conditional middle pattern into the file for next round 
                                if (!firstTime)
                                        middle<<endl;

                                firstTime = false;
                                middle<< pattLength <<"\t"<< linkTranversal->occur;

                                while( !pattern.empty()){
                                        middle <<"\t" <<pattern.top();
                                        pattern.pop();
                                }

                       }

                       if(!subPattern.empty())
                       {
                                middle<<endl;
                                middle<<subLength<<"\t" << subCount;
                                while( !subPattern.empty()){
                                        middle <<"\t" << subPattern.top();
                                        subPattern.pop();
                                }
                       }
                       linkTranversal = linkTranversal->nextLink;
                }
                middle.close();
                basePattern.push(i->event);
                BuildTree("middle.data", basePattern );
                basePattern.pop();
            }
       }
}



void BuildTree(char *sourceFile, stack<int> basePattern)
{

        bool OpenFile;
        int count;
	sequence seq, duplicate;

	//Next is Scan the database

        if (strcmp(sourceFile,"middle.data")==0)
		{
			// Build the condtional WAP tree
			runTime++;
                	//cout<<"Scan the intermediate database: " << runTime <<endl;
                	OpenFile = true;
        }
        else
        {
			// Build the original WAP tree
                	//cout<<"Scan the Original database.... " <<endl;
                	OpenFile = false;
        }

	ifstream ins ( sourceFile, ios::in);

	if ( !ins) {
		cerr << " File could not be opened\n";
		exit(1);
	}

	sequence::iterator point, eflag;
	int event, cid, number, seqNumber = 0;

	while (ins && !ins.eof())
	{
			// read the file and build the tree
                if(!OpenFile)
		    {
// read file from origional data file, read user id, event number from file
				ins >> cid;
                        ins >> number;
                        seqNumber++;
                        count = 1;
                }
                else
                {
				// read file from middle data file 
				ins >> number;
                        ins >> count;
                }

                duplicate.clear();

		    for(int i=0; i< number; i++)
                {
				ins >> event;

                        if ( duplicate.find(event) == duplicate.end())
                        {
        					duplicate.insert(sequence::value_type(event, 1));

	        				point = seq.find(event);
		        			eflag = seq.end();
			        
							if ( point != eflag)
	        					point ->second = point ->second + count;
        					else
	        					seq.insert( sequence::value_type(event,count));

						}
                }
	}

  // finish reading file, and next is going to filter out the event that does not meet the minimum support
        if (!OpenFile)
                frequency = (int)(minSupp* seqNumber);

        sequence::iterator i, bi;
        int n=0;

        for (  i = seq.begin(); i!=seq.end(); i++)
        {
                bi=i;
                if( bi != seq.begin())
                        bi--;
                else
                        bi= seq.begin();

                if (i->second < frequency)
                {
                        seq.erase(i);
                        i=bi;
                        n++;
                }
        }


        //cout<<"Finish scanning the database and Begin to build the tree ... " << endl;

	  // Next is build the WAP tree
        list<linkheader> lnkhdr;
        linkheader *newLinkHeader;
        bool newLink;
        node *Tranversal, *newNode, *Parent, *lastLinkage;
        node *root = new node;

        root->event = -1;
        root->occur = -1;
        root->parent = NULL;
        root->lSon = NULL;
        root->rSibling = NULL;
        root->nextLink = NULL;

        ifstream inFile ( sourceFile, ios::in);

	if ( !inFile) {
		cerr << " File could not be opened\n";
		exit(1);
	}

	while (inFile && !inFile.eof())
	{
		if ( !OpenFile)
                {
                        inFile >> cid;
                        inFile >> number;
                        count = 1;
                }
                else
                {
                        inFile >> number;
                        inFile >> count ;
                }

                Tranversal = root;

	  for(int i=0; i< number; i++)
        {
		//read event one by one, and insert the event into the tree
		    inFile >> event;

                    if(seq.find(event) != seq.end())
                    {
                        Parent = Tranversal;

                        list<linkheader>::iterator lnkBrow = lnkhdr.begin();
                        while (lnkBrow->event != event && lnkBrow != lnkhdr.end())
                               lnkBrow++;

                        if(lnkBrow == lnkhdr.end())
                                newLink = true;
                        else{
                                newLink = false;
                                lastLinkage = lnkBrow->lastLink;
                        }

                        if( Tranversal->lSon == NULL)
                        {
                                newNode = new node;
                                newNode->event = event;
                                newNode->occur = count;
                                newNode->lSon = NULL;
                                newNode->rSibling = NULL;
                                newNode->parent = Parent;
                                Tranversal->lSon = newNode;
                                if (newLink)
                                {
                                        newLinkHeader = new linkheader;
                                        newNode->nextLink=NULL;
                                        newLinkHeader->link = newNode;
                                        newLinkHeader->lastLink = newNode;
                                        newLinkHeader->event= event;
                                        lnkhdr.push_back(*newLinkHeader);
                                        free(newLinkHeader);
                                }
                                else
                                {
                                        lastLinkage->nextLink = newNode;
                                        lnkBrow->lastLink = newNode;
                                        newNode->nextLink = NULL;
                                }
                                Tranversal = newNode;
                        }
                        else
                        {
                                Tranversal = Tranversal->lSon;
                                if ( Tranversal->event == event)
                                        Tranversal->occur = Tranversal->occur + count;
                                else
                                {
                                        bool find= false;
                                        while(Tranversal->rSibling != NULL && !find )
                                        {
                                                Tranversal = Tranversal->rSibling;
                                                if ( Tranversal->event == event)
                                                {
                                                        Tranversal->occur = Tranversal->occur + count;
                                                        find = true;
                                                }
                                        }
                                        if (!find)
                                        {
                                                newNode = new node;
                                                newNode->event = event;
                                                newNode->occur = count;
                                                newNode->lSon = NULL;
                                                newNode->rSibling = NULL;
                                                newNode->parent = Parent;
                                                Tranversal->rSibling = newNode;
                                                if (newLink)
                                                {
                                                        newLinkHeader = new linkheader;
                                                        newNode->nextLink=NULL;
                                                        newLinkHeader->link = newNode;
                                                        newLinkHeader->lastLink = newNode;
                                                        newLinkHeader->event= event;
                                                        lnkhdr.push_back(*newLinkHeader);
                                                        free(newLinkHeader);
                                                }
                                                else
                                                {
                                                        lastLinkage->nextLink = newNode;
                                                        newNode->nextLink=NULL;
                                                        lnkBrow->lastLink = newNode;
                                                }
                                                Tranversal = newNode;
                                        }
                                }
                        }
                    }
                }
	}
        //cout<<"End of building tree and linkage..."<<endl;
        //cout<<"\n\nBegin the mining process:"<< runTime <<"\n"<<endl;
        MiningProcess(root, lnkhdr, basePattern);
        ofstream result("result_WAP.data", ios::app);

        //cout<<"Here is the result......"<<endl;
        while( !basePattern.empty())
        {
                result << basePattern.top() <<"\t";
                //cout << basePattern.top() <<"\t";
                basePattern.pop();
        }
        result<<endl;
        //cout<<"\n\n";
        return;

}
