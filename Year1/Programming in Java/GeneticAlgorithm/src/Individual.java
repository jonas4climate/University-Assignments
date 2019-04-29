//package com.bham.pij.assignments.a2a;

import java.util.Random;

public class Individual {
    
    // Chromosome contains genes as array of characters
    protected char[] chromosome;
    // Evaluation of how close the Individual is to the goal
    private double fitness;
    // Lower limit of ASCII values (for Weasel and BM)
    protected int upperASCIILimit;
    // Upper limit of ASCII values (for Weasel and BM)
    protected int lowerASCIILimit;
    // Alphabet of valid characters for Maths
    protected char[] mathsAlphabet = {'0','1','2','3','4','5','6','7','8','9','+','-','*','/'};


    // Constructor for random Individuals given an upper and lower ASCII value limit of valid characters
    // Creates Individuals with random values as genes
    public Individual(int length, int lowerASCIILimit, int upperASCIILimit) {
        chromosome = new char[length];
        this.upperASCIILimit = upperASCIILimit;
        this.lowerASCIILimit = lowerASCIILimit;

        for (int i = 0; i < chromosome.length; i++) {
            Random random = new Random();

            int randomPosition = random.nextInt(upperASCIILimit - lowerASCIILimit + 1) + lowerASCIILimit;
            chromosome[i] = (char) randomPosition;
        }
    }

    // Copies values from one Individual to another to avoid reading object-address instead of copying its values
    public Individual(Individual individual) {
        chromosome = individual.chromosome;
        fitness = individual.getFitness();
        upperASCIILimit = individual.upperASCIILimit;
        lowerASCIILimit = individual.lowerASCIILimit;
    }

    // Specific constructor to create Maths Individuals from the mathsAlphabet
    public Individual(int length) {
        chromosome = new char[length];

        for (int i = 0; i < chromosome.length; i++) {
            Random random = new Random();

            int randomGene = random.nextInt(mathsAlphabet.length);
            chromosome[i] = mathsAlphabet[randomGene];
        }
    }

    // Mutates a gene at given position "position" for Binary Maximiser - swaps 0's and 1's
    protected void mutateGeneBM(int position) {
        if (chromosome[position] == '1') {
            chromosome[position] = '0';
        } else {
            chromosome[position] = '1';
        }
    }

    // Mutates a gene at given position "position" for Weasel - move ASCII value closer to the goal
    protected void mutateGeneWeasel(int position, char[] goal) {
        if ((int) chromosome[position] <= (int) goal[position]) {
            chromosome[position]++;
        } else {
            chromosome[position]--;
        }
    }

    // Mutates a gene at given position "position" for Maths - change value to a new random value
    protected void mutateGeneMaths(int position) {
        Random random = new Random();

        int randomGene = random.nextInt(mathsAlphabet.length);
        // Change gene at position "position" to a new random value that is different from the value before
        if (mathsAlphabet[randomGene] != chromosome[position]) {
            chromosome[position] = mathsAlphabet[randomGene];
        } else {
            mutateGeneMaths(position);
        }
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    // Returns Individual as a single String (of all genes)
    public String toString() {
        String tempChromosome = "";
        for (int i = 0; i < chromosome.length; i++) {
            tempChromosome += chromosome[i];
        }
        return tempChromosome;
    }
}