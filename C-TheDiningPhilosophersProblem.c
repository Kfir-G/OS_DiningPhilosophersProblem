#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h> //for I/O
#include <stdlib.h> //for rand/strand
#include <windows.h> //for Win32 API
#include <time.h>    //for using the time as the seed to strand!

//5 Dining Philosophers
#define N 5

//Each Philosopher is Philosophising 5 rounds
#define MAX_PHILOSOPHY_ROUNDS 5
#define RIGHT (i + 1) % N
#define LEFT (i + 4) % N

#define MAX_SLEEP_TIME 500 //Miliseconds (1/2 second)
#define True 1
#define False 0

HANDLE mutex;
enum PHIL_STATES { THINKING, HUNGRY, EATING };

//state array, holds current state of each Philosopher in the System
int state[N];

//Array of Scheduling Constraints Semaphores - one for Each Philosopher
HANDLE sem[N];

//Function to Initialise and clean global data (mainly for creating the 
//Semaphore Haldles before all Threads start and closing them properly after all Threads finish!). 
int initGlobalData();
void cleanupGlobalData();

//Thread function for each Philosopher
DWORD WINAPI Philosopher(PVOID);

//better to write a generic function to randomise a Sleep time between 1 and 
//MAX_SLEEP_TIME msec
int calcSleepTime();

//Functions to manipulate Philosopher State - as defined in lecture notes
void take_forks(int);
void put_forks(int);
void test(int);


int main(int argc, char *argv[])
{
	HANDLE Phil_Arr[N];
	DWORD ID;
	//Array of unique IDs for Philosophers

	if (initGlobalData())
	{
		printf("Initialization Failed");
		//exit;
	}

	// Create all Philosopher Threads. Report if Error occurred!
	int Phil_Treads[N];
	for (int i = 0; i < N; i++)
	{
		Phil_Treads[i] = i;
		Phil_Arr[i] = CreateThread(NULL, 0, Philosopher, &Phil_Treads[i], 0, &ID);
		if (Phil_Arr[i] == NULL)
		{
			printf("main: Unexpected Error in enter trains thread %d creation", i);
			return 1;
		}
	}

	// Wait for ALL Philosophers to finish
	WaitForMultipleObjects(N, Phil_Arr, TRUE, INFINITE);
		// Report Completion
	printf("All philosophers completed");
	// Close all Thread Hndles and perform cleanup of Global Data, 
	// using cleanupGlobalData function
	cleanupGlobalData();
}

int initGlobalData() 
{
	mutex = CreateSemaphore(NULL, 1, 1, NULL);
	if (mutex == NULL) {
		printf("Create mutex error as follow: %d\n", GetLastError());
		return True;
	}
	for (int i = 0; i < N; i++)
	{
		sem[i] = CreateSemaphore(NULL, N, N, NULL);
			if (sem == NULL)
			{
				printf("Unexpected error in semaphore creation");
				return True;
			}
	}
	return FALSE;

}

void cleanupGlobalData() 
{
	CloseHandle(mutex);
	for (int i = 0; i < N; i++)
	{
		CloseHandle(sem[i]);

	}

}

void take_forks(int i)
{
	WaitForSingleObject(mutex, INFINITE);
	state[i] = HUNGRY;
	printf("Phill  %d: HUNGRY\n" , i);
	test(i);
	if (!ReleaseSemaphore(mutex, 1, NULL))
	{
		printf("Release mutex got error as follow: %d\n", GetLastError());
	}
    WaitForSingleObject(sem[i], INFINITE);

}

void put_forks(int i)
{
	WaitForSingleObject(mutex, INFINITE);
	state[i] = THINKING;
	printf("Phill  %d: THINKING\n", i);
	// test left and right neighbors
	test(LEFT); //try after with LEFT
	test(RIGHT);//try after with RIGHT
	if (!ReleaseSemaphore(mutex, 1, NULL))
	{
		printf("Release mutex got error as follow: %d\n", GetLastError());
	}

}

void test(int i) 
{
	if (state[i] == HUNGRY && state[LEFT] != EATING && state[RIGHT] != EATING)
	{
		state[i] = EATING;
		printf("Phill  %d: EATING\n", i);
	/*	if (!ReleaseSemaphore(sem[i], 1, NULL))			 //no need to take mutex in “test” function
		{
			printf("ReleaseMutex sem error: %d\n", GetLastError());
		}*/
	}
}

int calcSleepTime()
{
	int randtime=(rand() % MAX_SLEEP_TIME + 1);
	return randtime;
}

DWORD WINAPI Philosopher(PVOID Param)
{
	//Get the Unique ID from Param and keep it in a local variable
	int id = *(int*)Param;
//	int sleepPItem;


	//Loop for MAX_PHILOSOPHY_ROUNDS times. For Each iteration:
	for (int i = 0; i < MAX_PHILOSOPHY_ROUNDS; i++)
	{
		//sleepPItem = calcSleepTime();
		Sleep(calcSleepTime());
		take_forks(id);
		Sleep(calcSleepTime());
		put_forks(id);
	}

	return 0;

}

