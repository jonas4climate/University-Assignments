//package com.bham.pij.assignments.a2a;

public class BinaryMaximiser extends GAApplication {

    public BinaryMaximiser(int goal) {

        // Set size of population here
        population = new Individual[400];
        // Set length of Individuals here
        lengthOfIndividual = goal;
        // Set number of parents here
        numberOfParents = 160;
        // Set number of children here
        numberOfChildren = 160;
        // Set number of children here - REMARK: numberOfChildren mod numberOfParents must equal 0
        crossoverProbability = 0.93d;
        // Set mutation probability (to mutate a single gene) here
        mutationProbability = 0.001d;


        // Generates initial population with random values
        generatePopulation(lengthOfIndividual, 48, 49);
    }

    // Assigns fitness values to all Individuals
    @Override
    protected void assignFitness() {
        for (int i = 0; i < population.length; i++) {
            double individualFitness = 0;
            for (int j = 0; j < lengthOfIndividual; j++) {
                // if the gene is 1, add 1 to the fitness of the Individual
                if (population[i].chromosome[j] == '1') {
                    individualFitness += 1;
                } // else (for 0's) no change in value
            }
            // Set fitness for this Individual
            population[i].setFitness(individualFitness);
        }
    }

    // Performs mutation for all genes of all Individuals (under consideration of mutationProbability)
    @Override
    protected void mutation() {
        for (int i = 0; i < population.length; i++) {
            for (int j = 0; j < lengthOfIndividual; j++) {
                // Check if mutation for this gene is supposed to happen
                if (Math.random() <= mutationProbability) {
                    // Mutate this specific gene in Individual class
                    population[i].mutateGeneBM(j);
                }
            }
        }
    }

    // Testing method to observe algorithm during performance
    public static void main (String[] args) {
        final int maxIter = 1000;
        System.out.println("Maximum iterations" + maxIter);
        BinaryMaximiser binaryMaximiser = new BinaryMaximiser(1000);
        int j = 0;
        for (int i = 0; i < maxIter && binaryMaximiser.getBest().getFitness() < maxIter; i++) {
            binaryMaximiser.run();
            System.out.println(binaryMaximiser.getBest().getFitness());
            j++;
        }
        System.out.println("Iteration number: " + j);
        System.out.println(binaryMaximiser.getBest().toString());
        System.out.println(binaryMaximiser.getBest().getFitness());
    }
}
