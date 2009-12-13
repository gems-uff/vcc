/*
This is the PLWAP algorithm program, which demostartes the 
result described in:
C. I. Ezeife and Y. Lu. "Mining Web Log sequential Patterns with 
Position Coded Pre-Order Linked WAP-tree" in DMKD.

1.DEVELOPMENT ENVIRONMENT:
Although initial version is developed under the 
hardware/software environment specified below, the program
 runs on more powerful and faster multiprocessor UNIX environments as well.
        (1)Hardware: Intel Celeron 400 PC, 64M Memory;
        (2)Operting system: Windows 98;
        (3)Development tool: Inprise(Borland) C++ Builder 6.0.
Note: The algorithm is developed under C++ Builder 6.0. However, 
it is possible to compile and run the pro
gram under any standard C++ development tool.
Program is in plwap.cpp, compiled with Unix "g++ plwap.cpp" and 
executed with a.out.

2. INPUT:
        (1) test.data:
For simplifying input process of the program, we assume that 
all input data have been preprocessed
 such that all events belonging to same user
id have been gathered together, and formed as a sequence 
which is saved in a text file, called, "test.data". The 
"test.data" file is composed of
hundreds of thousands of lines of sequences where each line 
represents a web access sequence for each user.

Every line of the input data file ("test.data") includes UserID, length
of sequence and the sequence which are seperated by tab spaces.
An example input line is:
100     5       10      20      40      10      30
Here, 100 represents UserID, 5 means the length of sequence is 5, the
sequence is 10,20,40,10,30.

(2) minimum support:
The program also needs to accept a value between 0 and 1 which 
is called minimum support. The minimum support input is entered 
interactively by the user during
the execution of the program when prompted. For a minimum 
support of 50%, user should type 0.5, and for minsupport of 5%, 
user should type .05, and so on.
3. OUTPUT: result_PLWAP.data
Once the program terminates, we can find the result frequent 
patterns in a file named "result_PLWAP.data".
it may contain lines of patterns. Each line represents a pattern.

4. FUNCTIONS USED IN THE CODE:
   (1)BuildTree: Builds the PLWAP tree
   (2)BuildLinkage: Builds the linkage for PLWAP tree
   (3)makeCode: Makes the position code for a node
   (4)checkPosition: Checks the position between any two nodes in 
the PLWAP tree
(5)MiningProcess: Mines sequential frequent patterns from the PLWAP tree

5. DATA STRUCTURE
  Three struct are used in this program:
(1) the node struct indicates a PLWAP node which contains the 
information:
                a.the event name
                b.the number of occurrence of the event
                c. a link to the position code
                d. length of position code
                e. the linkage to next node same event name in PLWAP tree
                f. a pointer to its left son
                g. a pointer to its right sibling
                h. a pointer to its parent
                i. the number of its sons.
        (2) a position code struct
        (3) a linkage struct

6. ADDITIONAL INFORMATION:
The run time is displayed on the screen with start time, end time 
and total seconds for running the program.
*/

#include <iostream>
#include <map>
#include <fstream>
#include <list>
#include <queue>

#define min(a, b) ((a) < (b) ? (a) : (b))  // simple inline function

using namespace std;

struct positionCode
{
        unsigned int code;
	//position code is composed of a series of binary
        positionCode *next;		
	//if the length of position code is longer than 32
	//we use a link to next 32 bits
};

struct node
{	//  Structure of a node in the PLWAP tree:
  	int event;	//	event name of the node 
  	int occur;	//	occurence for the node
  	int pcLength;	//	the length of position code for the node
	positionCode *pcCode;	//	the pointer of the position code
	int CountSon;	//	the sum of occurence of sons
  	node *nextLink;	
	//	the linkage to next node with same event name
  	node *lSon;	//	the pointer to its left Son
  	node *rSibling;	//	the pointer to its right sibling
  	node *parent;	//	the poniter to its parent
};

struct linkheader
{			//Structure of linkage header:
        int event;	//	event name of the linkage
        int occur;	//	occurence of the node in tree 
        node *link;	//link to the first occurrence of the event in tree
        node *lastLink;	//	link to the last occurrence of the event in tree
};

typedef multimap<int, int, less<int> > sequence;  
			// used to store the sequence read from the input file

list<linkheader> lnkhdr; 
			// all linkage headers are stored in a list(table)
node *root;		// root of whole tree
int frequency;		// occurrence number can be considered frequent
int seqNumber;		// number of sequence in database
float minSupp;		// minimum support between 0--1

			//following are functions used for the PLWAP mining.

void printtree(node*);
			// For testing: print the whole PLWAP tree 
void printLinkage (list<linkheader>);
			// For testing: print the linkage
void BuildTree(char*);
			// Build the PLWAP tree
void BuildLinkage(node *);
positionCode* makeCode(int, positionCode*, bool);	
			// Make position code for a node
int checkPosition(positionCode*, int, positionCode*, int);	
			// Check the position between two nodes
void MiningProcess(list<node*>, queue<int>, int );			
			// Mine sequential pattern from PLWAP tree


int main(int argc, const char* argv[])
{
	list<node*> newRootSet;
	queue<int> beginPattern;

	//cout << "please enter the minimum support:";  
			//prompt to enter the minimum support
	//cin >> minSupp;
			//minSupp=0.00005;
   
    minSupp = atof(argv[1]);

	ofstream result("result_PLWAP.data", ios::trunc); 
			// open the result file for writing
	result.close();

        clock_t start, end;
        // double cpu_time_used;

	int tim1 = time(0);

			//cout<<"Now start the program......\n\n"<<endl;

	BuildTree("test.data"); 	
			// Build the PLWAP tree 
	newRootSet.push_back(root);

			//cout<<"\n\nBegin the mining process"<<endl;

	MiningProcess(newRootSet, beginPattern, seqNumber);

	int tim2 = time(0);
	// end = clock();
        // cpu_time_used = ((double) (end - start)) / CLOCKS_PER_SEC;

	cout<<"\n\nbegin time: "<<tim1<<'\n';
	cout<<"end time  : "<<tim2<<'\n';
	cout<<"The execution time is: "<< tim2-tim1<< endl;
	// cout<<"The CPU time used is : "<< cpu_time_used << endl;
	cout<<"\n\nEnd the program"<<endl;

      //ofstream ExecuteTime("PlwapExecuteTime.txt", ios::app);
	//ExecuteTime<<"\n\nRunning Result at Minimum Support at "<<minSupp;
      //ExecuteTime<<"\n\nbegin time: "<<tim1<<'\n';
      //ExecuteTime<<"end time  : "<<tim2<<'\n';
      //ExecuteTime<<"The execution time is:\n"<< tim2-tim1;
      //ExecuteTime<<"\n\nEnd the program"<<endl;
	//ExecuteTime.close();

	return 0;
}

void printtree(node *start)
{
// For testing: print the whole PLWAP tree 
        if (start->lSon !=NULL)
        {
                cout<<"event:"<<start->event<<" occurrence = "<< start->occur<< ".  the son is "<<(start->lSon)->event;
                if(start->event != -1) //cout<<".... its parent is "<<(start->parent)->event;
                cout<<endl;
                printtree(start->lSon);
        }

        if (start->rSibling != NULL)
        {
                cout<<"event:"<<start->event<<" the sibling is "<< (start->rSibling)->event << ";  the occrrence are "<<(start->rSibling)->occur<<endl;
                printtree(start->rSibling);
        }
}

void printLinkage(list<linkheader> table)
{
// For testing: print the linkage of the PLWAP tree 

        list<linkheader>::iterator pnt = table.begin();
        while (pnt != table.end())
        {
                cout<<pnt->event;
                node *tr= pnt->link;
                while (tr !=NULL)
                {
	                cout<<"-->"<<tr->event<<"("<<tr->occur<<","<<tr->pcLength<<","<<(tr->pcCode)->code<<")";
	                tr=tr->nextLink;
                }
        cout<<endl;
        pnt++;
  }

}

void MiningProcess(list<node*> rootSet, queue<int> basePattern, int Count)
{
/*
MiningProcess is the function for finding the patterns from PLWAP tree
Called in Parameters:
	(1) rootSet: the set of roots for mining the current tree. The roots are stored into a list.
	(2) basePattern: is the subsequence which is obtained in previous round
	(3) Count: is the number which is sum of occurrence of suffix tree
*/

		list<linkheader>::iterator pnt;

        //cout<<"\nEntering the mining process " <<endl;

        for( pnt = lnkhdr.begin(); pnt != lnkhdr.end(); pnt++)
        {
                list<node*> newRootSet;
                node * SavePoint = NULL;
                bool DescSave = false;
                bool RootUsed = false;
                int totalSon = 0;
                int count = 0;
                int emptySon = 0;
                int RootCount = Count;

                node * linkBrow = pnt -> link;
                list<node*>::iterator rootBrow = rootSet.begin();

                while (linkBrow != NULL && rootBrow != rootSet.end() && RootCount >= frequency )
                {
                        int check = checkPosition(((*rootBrow)->lSon)->pcCode, (*rootBrow)->pcLength+1, linkBrow->pcCode, linkBrow->pcLength);
						/*
			(1) 0: FirstNode is the ancestor of SecondNode
			(2) 1: FirstNode is in the left-tree of SecondNode
			(3) 2: FirstNode is in the right-tree of SecondNode
			(4) 3: FirstNode is the descendant of SecondNode
						*/

                        switch ( check ){
                        case 0:
                                if ( SavePoint != NULL )
                                {
                                        if ( SavePoint->lSon == NULL )
                                                DescSave = false;
                                        else
                                                if (checkPosition( (SavePoint->lSon)->pcCode, SavePoint->pcLength+1, linkBrow->pcCode, linkBrow->pcLength)== 0)
                                                        DescSave = true;
                                                else DescSave = false;
                                }

                                if ( !DescSave)
                                {
                                        count = count + linkBrow->occur;
                                        totalSon = totalSon + linkBrow->CountSon;
                                        RootUsed = true;
                                        SavePoint = linkBrow;
                                        if (linkBrow->lSon != NULL)
                                                newRootSet.push_back(linkBrow);
                                        else
                                                emptySon = emptySon + linkBrow->occur;
                                }
                                linkBrow = linkBrow->nextLink;
                                break;
                        case 1:
                                rootBrow++;
                                RootUsed = false;
                                break;
                        case 2:
                                linkBrow = linkBrow->nextLink;
                                break;
                        case 3:
                                linkBrow = linkBrow->nextLink;
                                break;
                        }
                }

                if ( count >= frequency)
                {
                        queue<int> tempPattern = basePattern;

                        tempPattern.push(pnt->event);
                        queue<int> otherPattern = tempPattern;

                        ofstream result("result_PLWAP.data", ios::app);
                        FILE *txt = fopen("result_PLWAP.data", "a+");
                        fprintf(txt, "%s\n", buffer);


                        while(!tempPattern.empty())
                        {
                                result<<tempPattern.front()<<";";
                                tempPattern.pop();
                        }
                        result<<(float)count/(float)seqNumber;
                        result<<endl;

                        if ( totalSon >= frequency)
                                MiningProcess(newRootSet, otherPattern, count-emptySon);
                }
        }
        return;
}



void BuildTree(char *sourceFile)
{
	sequence seq, duplicate;

//Next is Scan the database
	//cout<<"Scan the database first time: " <<endl;
	ifstream ins ( sourceFile, ios::in);

	if ( !ins) {
		cerr << " File could not be opened\n";
		exit(1);
	}

	sequence::iterator point, eflag;
	int event, cid, number;
	seqNumber = 0;

	// next while loop is going to read sequences from file into the sequence data structure
	while (ins && !ins.eof())
	{
		 
		ins >> cid;
         ins >> number;
         seqNumber++;

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
	        			point ->second ++;
        		else
	        			seq.insert( sequence::value_type(event,1));

		    }
        }
	}

	// next is going to filter out the event that does not meet the minimum support
        frequency = (int)(minSupp* seqNumber);

        sequence::iterator i, bi;

		i = seq.begin();
        while( i!=seq.end())
        {
            bi = i;
			if( i != seq.end())
			{
                 bi++;
			}

            if (i->second < frequency)
            {
                seq.erase(i);
                i=bi;
            }
			else
				i++;
        }
        //cout<<"Finish scanning the database: " <<endl;

// Next is build the PLWAP tree

        node *Tranversal, *newNode, *Parent;

        root = new node;
        root->event = -1;
        root->occur = seqNumber;
        root->CountSon = 0;
        root->pcLength = 0;
        root->parent = NULL;
        root->lSon = NULL;
        root->rSibling = NULL;
        root->nextLink = NULL;
        root->pcLength = 0;

        ifstream inFile ( sourceFile, ios::in);

		if ( !inFile) {
			cerr << " File could not be opened\n";
			exit(1);
		}

		//cout <<"Begin to build tree " << endl;

		while (inFile && !inFile.eof())
		{
			inFile >> cid;
			inFile >> number;

			Tranversal = root;

			for(int i=0; i< number; i++)
			{
				inFile >> event;

				if(seq.find(event) != seq.end())
				{
                        Parent = Tranversal;

                        if( Tranversal->lSon == NULL)						// inesrt first child of root
                        {
                                newNode = new node;
                                newNode->event = event;
                                newNode->occur = 1;
                                newNode->lSon = NULL;
                                newNode->rSibling = NULL;
								newNode->nextLink = NULL;
                                newNode->CountSon = 0;
                                Parent->CountSon ++;
                                newNode->parent = Parent;
                                newNode->pcLength = Tranversal->pcLength + 1;
                                newNode->pcCode = makeCode(Tranversal->pcLength,Tranversal->pcCode,true);
                                Tranversal->lSon = newNode;
                                Tranversal = newNode;
                        }
                        else
                        {						
                                Tranversal = Tranversal->lSon;
                                if ( Tranversal->event == event)								// the node is found
                                {
                                        (Tranversal->parent)->CountSon++;
                                        Tranversal->occur++;
                                }
                                else
                                {
                                        bool find= false;
                                        while(Tranversal->rSibling != NULL && !find )										//find the event in its sibling
                                        {
                                                Tranversal = Tranversal->rSibling;
                                                if ( Tranversal->event == event)
                                                {
                                                        Tranversal->occur ++;
                                                        (Tranversal->parent)->CountSon++;
                                                        find = true;
                                                }
                                        }
                                        if (!find)										//insert a new node
                                        {
                                                newNode = new node;
                                                newNode->event = event;
                                                newNode->occur = 1;
                                                newNode->lSon = NULL;
                                                newNode->rSibling = NULL;
												newNode->nextLink = NULL;
												newNode->CountSon = 0;
                                                Parent->CountSon ++;
                                                newNode->parent = Parent;
                                                newNode->pcLength = Tranversal->pcLength + 1;
                                                newNode->pcCode = makeCode(Tranversal->pcLength,Tranversal->pcCode, false);
                                                Tranversal->rSibling = newNode;
                                                Tranversal = newNode;
                                        }
                                }
                        }
				}
			}
		}

		//next tranversing all tree to build the Pre-order linkage
        linkheader *newLinkHeader;
        for (  i = seq.begin(); i!=seq.end(); i++)			//form a header table
        {
                newLinkHeader = new linkheader;
                newLinkHeader->link = NULL;
                newLinkHeader->lastLink = NULL;
                newLinkHeader->event= i->first;
                newLinkHeader->occur= i->second;
                lnkhdr.push_back(*newLinkHeader);
                free(newLinkHeader);
        }
        //cout<<"End of building tree and begin to build linkage..."<<endl;
        BuildLinkage(root->lSon);
        //cout<<"End of building linkage...\n\n";

        //cout<<"Print the WAP tree"<<endl;
//        printtree(root);
        //cout<<"\nEnd of printing WAP tree and begin printing linkage"<<endl;
//        printLinkage(lnkhdr);

        return;
}

void BuildLinkage(node *start)
{
/*
BuildLinkage is the fundtion to link the event with same label together in the tree.
It follows the pre-order style. 
Called in paremeter: the current node to be linked.
*/
        if (start !=NULL)
        {
                list<linkheader>::iterator lnkBrow = lnkhdr.begin();
                while (lnkBrow->event != start->event && lnkBrow != lnkhdr.end())
                        lnkBrow++;

                node *lastLinkage;
                lastLinkage = lnkBrow->lastLink;
                if (lastLinkage == NULL )
                        lnkBrow->link = start;
                else
                        lastLinkage->nextLink = start;
                lnkBrow->lastLink = start;

                BuildLinkage(start->lSon);
                BuildLinkage(start->rSibling);
        }
        else return;
}


positionCode * makeCode(int length, positionCode *pCode, bool addOne)
{
/*
makeCode is the function to genearte a position code for a node
Call in Parameters:
	(1) length: is the length of position code
	(2) pCode: is the position code of its parent or its nearest left 
	sibling.
	(3) addOne: is a boolean to indicate the pCode is the position code of 
	its parent or its nearest left sibling. If addOne is true, 
	then the  pCode is the position code of its parent, otherwise 
	it is the position code of its nearest left sibling.
Return value:
	The pointer of the new position code
*/
		positionCode *start, *browser, *newPC;
        int leftCount = length % 32;
        int linkCount = (int)(length / 32);

        if ( linkCount == 0 )
        {
                start = new positionCode;
                start->next = NULL;
                if (length == 0)
                        start->code = 1 << 31;
                else
                        if (addOne)
                                start->code = pCode->code | (1<<(31-leftCount));
                        else
                                start->code = pCode->code ;
                return start;
        }
        else
        {
                newPC = new positionCode;
                newPC->code = pCode->code;
                pCode = pCode ->next;
                start = newPC;
                browser = start;

                for(int i = 1; i < linkCount; i++)
                {
                        newPC = new positionCode;
                        newPC->code = pCode->code;
                        browser-> next = newPC;
                        browser = newPC;
                        pCode = pCode->next;
                }

                if (leftCount == 0)
                {
                        newPC = new positionCode;
                        if (addOne)
                                newPC->code = 1 << 31;
                        else
                                newPC->code = 0 << 31;
                        newPC->next = NULL;
                        browser->next = newPC;
                }
                else
                {
                        newPC = new positionCode;

                        if (addOne)
                                newPC->code = pCode->code | (1<<(31-leftCount));
                        else
                                newPC->code = pCode->code;

                        newPC ->next = NULL;
                        browser->next = newPC;
                }
        }

        return start;
}


int checkPosition(positionCode *FirstNode, int aLength, positionCode *SecondNode, int dLength)
{
/*
checkPosition function 
Call in parameters: 
	(1) the FirstNode's position code; 
	(2) the length of FirstNode's positon code;
	(3) the SecondNode's position code; 
	(4) the length of SecondNode's positon code.
Return Values: one of the values list below
	(1) 0: FirstNode is the ancestor of SecondNode
	(2) 1: FirstNode is in the left-tree of SecondNode
	(3) 2: FirstNode is in the right-tree of SecondNode
	(4) 3: FirstNode is the descendant of SecondNode

*/

        if (aLength == 1 )	// The length of FirstNode is 1, which means
                return 0;	
			// the FirstNode is root of whole tree, every node
			// is desendant of it.

        int length = min(aLength,dLength);  
			// determine which length of position code is bigger
        int linkCount = (int)(length / 32);
        int leftCount = length % 32;

        for( int i = 0; i < linkCount; i++)
                if ( FirstNode->code > SecondNode->code)
                        return 1;
                else
                        if ( FirstNode->code < SecondNode->code)
                                return 2;
                        else
                        {
                                FirstNode = FirstNode->next;
                                SecondNode = SecondNode->next;
                        };


        unsigned int aCode, dCode;
        if ( aLength <= dLength )
        {
                if (leftCount == 0)
                        return 0;

                aCode = FirstNode -> code >> ( 32 - leftCount );
                dCode = SecondNode -> code >> ( 32 - leftCount );
                if (aCode == dCode )
                        return 0;
                else
                        if (aCode < dCode)
                                return 2;
                        else return 1;
        }
        else
        {
                if (leftCount == 0)
                        dCode = 1;
                else
                        dCode = ( SecondNode -> code >> ( 31 - leftCount )) | 1 ;
                aCode = FirstNode -> code >> ( 31 - leftCount );


                if ( aCode == dCode )
                        return 3;
                else
                        if (aCode < dCode)
                                return 2;
                        else return 1;

        }

}

