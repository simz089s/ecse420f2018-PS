#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <stdbool.h>
#include <pthread.h>
#include <limits.h>
#include <semaphore.h>

sem_t forks[5];
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
sem_t stopped;
int eaters = 0;

static int numForks = sizeof(forks)/sizeof(sem_t);

void get_waiter_permission()
{
    pthread_mutex_lock(&mutex);
    eaters++;
    if (eaters >= numForks)
    {
        pthread_mutex_unlock(&mutex);
        sem_wait(&stopped);
    }
    else
    {
        pthread_mutex_unlock(&mutex);
    }
}

void inform_waiter()
{
    pthread_mutex_lock(&mutex);
    eaters--;
    if (eaters == numForks-1)
    {
        sem_post(&stopped);
    }
    pthread_mutex_unlock(&mutex);
}

// Philosopher i gets the fork
void get_fork(int i)
{
    get_waiter_permission();
    sem_wait(&forks[i]);
    sem_wait(&forks[(i+1)%numForks]);
}

// Philosopher releases the fork
void put_fork(int i)
{
    sem_post(&forks[i]);
    sem_post(&forks[(i+1)%numForks]);
    inform_waiter();
}

void* philosopher(void* ph_id)
{
    int id = *(int*)ph_id;
    while (true)
    {
        get_fork(id);
        printf("Philosopher %d eating\n", id);
        sleep(rand() % 6 + 1);
        put_fork(id);
        printf("Philosopher %d thinking\n", id);
        sleep(rand() % 6 + 1);
    }
    return NULL;
}

int main()
{
    srand(time(NULL));
    for (int i = 0; i < numForks; i++)
    {
        sem_init(&forks[i], 0, 1);
    }
    sem_init(&stopped, 0, 0);
    int ids[numForks];
    pthread_t philosophers[numForks];
    for (int i = 0; i < numForks; i++)
    {
        ids[i] = i;
        pthread_create(&philosophers[i], NULL, philosopher, (void*)(ids+i));
    }
    for (int i = 0; i < numForks; i++)
    {
        pthread_join(philosophers[i], NULL);
    }
    for (int i = 0; i < numForks; i++)
    {
        sem_destroy(&forks[i]);
    }
    pthread_mutex_destroy(&mutex);
    sem_destroy(&stopped);
    return EXIT_SUCCESS;
}
