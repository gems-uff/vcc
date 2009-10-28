/*
This is the GSP algorithm program which is to demostarte the result described 
in: R. Srikant and R. Agrawal. "Mining sequential patterns: Generalizations 
and performance improvements",  1996.

1.DEVELOP ENVIRONMENT:
Although initial version is developed under the
hardware/software environment specified below, the program
 runs on more powerful and faster multiprocessor UNIX environments as well.
	(1)Hardware: Intel Celeron 400 PC, 64M Memory;
	(2)Operting system: Windows 98;
	(3)Development tool: Inprise(Borland) C++ Builder 6.0.
Note: The algorithm is developed under C++ Builder 6.0. However, it is possible 
to compile the program under any standard C++ development tools and 
run it.
Program is in GSP.cpp, compiled with Unix "g++ GSP.cpp" and 
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
100	5	10	20	40	10	30.  
100 represents UserID, 5 means the length of sequence is 5, the sequence is
10,20,40,10,30. 

(2) minimum support:
The program also needs to accept a value between 0 and 1 which is called minimum support. 
The minimum support is prompted to enter when the program starts.

3. OUTPUT: result_GSP.data
Once the program terminates, we can find the result patterns in a file named 
"result_GSP.data".
It may contains lines of patterns. Each line represents a pattern.
4. METHODS:
	(1)GSP: read the file and mining

5. ADDTIONAL INFORMATION: 
The run time is display on the screen with start time, end time and total 
seconds for running the program.
*/

#include <iostream>
#include <map>
#include <fstream>
#include <list>
#include <deque>

using namespace std;

struct candidate{
        deque<int> sequence;
        int count;
};

typedef multimap<int, int, less<int> > sequence;
int frequency;
float minSupp;
bool finish;
char *sourceFile = "test.data";
int runTime = 0;

void GSP();
list<candidate> Test(list<candidate>);


int main()
{
	  //cout << "please enter the frequency:";
	  //cin >> minSupp;
	  minSupp = 0.3;
	  //minSupp = 0.75;

        ofstream result("result_GSP.data", ios::trunc);
        result.close();

        int tim1 = time(0);

        //cout<<"Now start the program......\n\n"<<endl;

        GSP();

        int tim2 = time(0);

        cout<<"\n\nbegin time: "<<tim1<<'\n';
        cout<<"end time  : "<<tim2<<'\n';
        cout<<"The execution time is:\n"<< tim2-tim1;
        cout<<"\n\nEnd the program"<<endl;

        return 0;
}



void GSP()
{
	sequence seq, duplicate;
    finish = false;
//Next is Scan the database

	ifstream ins ( sourceFile, ios::in);

	if ( !ins) {
		cerr << " File could not be opened\n";
		exit(1);
	}

	sequence::iterator point, eflag;
	int event, cid, number, seqNumber = 0;

        //cout<<"Scan the original database once to find the 1-sequence" << endl;

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
	        			( point ->second )++;
        			else
	        			seq.insert( sequence::value_type(event,1));

		    }
		}

	}

        frequency = (int)(minSupp* seqNumber);

        sequence::iterator i, bi;

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
                }
        }

//        //cout<<"Found 1-sequence .....\n";
        ofstream result("result_GSP.data", ios::app);
        for( i = seq.begin(); i != seq.end(); i++ )
                result<< i->first << endl;
//                //cout<< i->first << endl;

// Next is generate 2-sequence
        //cout<<"\nGenerating 2-sequence....."<<endl;
        list<candidate> candList;

        for( i = seq.begin(); i != seq.end(); i++ )
                for ( bi = seq.begin(); bi != seq.end(); bi++ )
                {
                        struct candidate newCand;
                        newCand.count = 0;
                        newCand.sequence.push_front(i->first);
                        newCand.sequence.push_back(bi->first);
                        candList.push_back(newCand);
                }

        runTime = 2;
        do{
                candList = Test(candList);

        }while(!finish);

        return;

}

list<candidate> Test(list<candidate> candList)
{
// next is test seqeunce is frequent or not
        int event, cid, number;

	ifstream inFile ( sourceFile, ios::in);

	if ( !inFile) {
		cerr << " File could not be opened\n";
		exit(1);
	}

        //cout<<"Testing "<<runTime<<"-sequence..."<<endl;

	while (inFile && !inFile.eof())
	{
                deque<int> inSequence;
	        inFile >> cid;
                inFile >> number;

                for (int i = 0; i < number; i++)
                {
                        inFile >> event;
                        inSequence.push_back(event);
                }

                for ( list<candidate>::iterator candBrow = candList.begin(); candBrow != candList.end(); candBrow++)
                {
                        deque<int> candSequence = candBrow->sequence;
                        unsigned int j = 0, k = 0;
                        bool find = false;

                        while (  j < candSequence.size() && k < inSequence.size() && !find )
                        {
                                if ( candSequence[j] == inSequence[k])
                                {
                                        j++;
                                        if ( j == candSequence.size())
                                                find = true;
                                }
                                k++;
                        }

                        if ( find )
                                candBrow->count ++;
                }
        }

        list<candidate>::iterator candBrow = candList.begin();

        while( candBrow != candList.end() )
        {
                if ( candBrow->count < frequency ){
                        candBrow = candList.erase(candBrow);
                }
                else candBrow++;
        }

//        //cout<<"Finish testing "<<runTime<<"-sequence. and This is result..."<<endl;
        ofstream result("result_GSP.data", ios::app);
        for ( candBrow = candList.begin(); candBrow != candList.end(); candBrow++)
        {
                deque<int> candSequence = candBrow->sequence;
                for ( int i = 0; i< candSequence.size(); i++)
                      if( i == candSequence.size() - 1)
                        result<< candSequence[i];
                      else
                        result<< candSequence[i]<<";";
                result<<endl;
//                        //cout<< candSequence[i]<<"\t";
//                //cout<<endl;
        }

        runTime++;
        list<candidate> newCandList;
        if( candList.size() == 0 )
                finish = true;
        else
        {
                //cout<<"\nGenerating "<<runTime<<"-sequence....."<<endl;
                list<candidate>::iterator oneBrowser, twoBrowser;
                for ( oneBrowser = candList.begin(); oneBrowser != candList.end(); oneBrowser++)
                {
                        deque<int> firstSeq = oneBrowser->sequence;
                        for ( twoBrowser = candList.begin(); twoBrowser != candList.end(); twoBrowser++)
                        {
                                deque<int> secondSeq = twoBrowser->sequence;
                                if( firstSeq[1] == secondSeq[0])
                                {
                                        int j = 1;
                                        int length = firstSeq.size();
                                        bool match = true;
                                        while( j < length-1 && match )
                                        {
                                                if (firstSeq[j+1] != secondSeq[j])
                                                        match = false;
                                                j++;
                                        }

                                        if (match)
                                        {
                                                struct candidate newCand;
                                                secondSeq.push_front(firstSeq.front());
                                                newCand.sequence = secondSeq;
                                                newCand.count = 0;
                                                newCandList.push_back(newCand);
                                        }
                                }
                        }

                }

        }
        return newCandList;
}
