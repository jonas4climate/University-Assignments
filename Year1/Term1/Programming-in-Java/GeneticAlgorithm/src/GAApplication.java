//package com.bham.pij.assignments.a2a;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

// Genetic Algorithm - general class
public abstract class GAApplication {

    protected Individual[] population;
    protected int lengthOfIndividual;
    protected int numberOfParents;
    protected int numberOfChildren;
    protected double crossoverProbability;
    protected double mutationProbability;


    // General run-method for all Genetic Algorithms
    // Performs mutation, assigns fitnesses, sorts the population and 
    // the reproduction (including crossover) process
    public void run() {
        mutation();
        assignFitness();
        sort(population, 0, population.length - 1);
        reproduction();
        assignFitness();
        sort(population, 0, population.length - 1); 
    }

    // Overridden version used for each specified Genetic Algorithm
    protected void mutation() {

    }

    // Overridden version used for each specified Genetic Algorithm
    protected void assignFitness() {

    }



    // Mergesort methods merge() and sort() - sorting Individuals based on their fitness (ascending)


    // Merges the subarrays L[] and R[]
    private void merge(Individual population[], int l, int m, int r) {
        // Find sizes of two subarrays to be merged
        int p1 = m - l + 1;
        int p2 = r - m;

        // Create temporary arrays and copy values to them
        Individual L[] = new Individual[p1];
        for (int i = 0; i < p1; ++i) {
            L[i] = new Individual(population[l + i]);
        }
        Individual R[] = new Individual[p2]; 
        for (int j = 0; j < p2; ++j) {
            R[j] = new Individual(population[m + 1 + j]);
        }

        // Initial indexes of first and second subarrays 
        int i = 0;
        int j = 0; 
        // Initial index of merged subarry array 
        int k = l; 

        // Merging
        while (i < p1 && j < p2) { 
            if (L[i].getFitness() <= R[j].getFitness()) {
                population[k] = new Individual(L[i]); 
                i++; 
            } else {
                population[k] = new Individual(R[j]); 
                j++; 
            }
            k++; 
        }

        // Copy remaining elements of L[]
        while (i < p1) { 
            population[k] = new Individual(L[i]); 
            i++; 
            k++; 
        }
        // Copy remaining elements of R[]
        while (j < p2) { 
            population[k] = new Individual(R[j]); 
            j++; 
            k++; 
        } 
    }
    
    // Main method to sort - calls merge
    private void sort(Individual population[], int l, int r) {
        if (l < r) {
            // Set middle
            int m = (l + r)/2;

            // Sort first and second half
            sort(population, l, m);
            sort(population, m + 1, r);

            // Merge the sorted halves
            merge(population, l, m, r);
        }
    }


    // Generate population with size of Individuals and both lower and upper limit of valid ASCII-values
    protected void generatePopulation(int length, int lowerASCIILimit, int upperASCIILimit) {
        for (int i = 0; i < population.length; i++) {
            population[i] = new Individual(length, lowerASCIILimit, upperASCIILimit);
        }
    }

    private void reproduction() {
        // Generates random indexes to pair Individuals as random parent pairs
        ArrayList<Integer> randomNumber = new ArrayList<Integer>();
        for (int i = 0; i < numberOfParents; i++) {
            randomNumber.add(i);
        }
        Collections.shuffle(randomNumber);

        // Calculates ratio of children to parents
        // REMARK: Will "round" (lossy) because of integer division
        int ratio = numberOfChildren / numberOfParents;

        // Set size of parent and child arrays
        Individual[] parent = new Individual[2];
        Individual[] child = new Individual[ratio * 2];

        // For all parent-pairs
        for (int j = 0; j < numberOfParents; j += 2) {
            // Check if crossover is supposed to happen
            if (Math.random() <= crossoverProbability) {
                // Set parents as Individuals of the end of the population (best Individuals)
                parent[0] = new Individual(population[population.length - 1 - randomNumber.get(j)]);
                parent[1] = new Individual(population[population.length - 1 - randomNumber.get(j + 1)]);

                // Create all children
                for (int k = 0; k < ratio * 2; k++) {
                    child[k]= new Individual(lengthOfIndividual, parent[0].lowerASCIILimit, parent[0].upperASCIILimit);
                }

                // Do (ratio amount of times), for each iteration 2 children will receive their genes from the parents
                for (int k = 0; k < ratio * 2; k += 2) {
                    Random random = new Random();
                    // Get random crossover position
                    int randomPosition = random.nextInt(lengthOfIndividual);

                    // Perform crossover-copying
                    for (int l = 0; l < randomPosition; l++) {
                        child[k].chromosome[l] = parent[0].chromosome[l];
                        child[k + 1].chromosome[l] = parent[1].chromosome[l];
                    }
                    for (int l = randomPosition; l < lengthOfIndividual; l++) {
                        child[k].chromosome[l] = parent[1].chromosome[l];
                        child[k + 1].chromosome[l] = parent[0].chromosome[l];
                    }
                }
                // Store children in beginning of the population (overwrite worst Individuals)
                for (int k = 0; k < ratio * 2; k++) {
                    population[j + k] = new Individual(child[k]);
                }
            }
        }
    }
    
    // Returns best Individual
    public Individual getBest() {
        return population[population.length - 1];
    }
}