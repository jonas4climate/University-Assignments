//package com.bham.pij.assignments.a2a;

public class Weasel extends GAApplication {

    public char[] goal;

    public Weasel(String goal) {
        // Set size of population here
        population = new Individual[1600];
        // Set length of Individuals here
        lengthOfIndividual = goal.length();
        // Target string goal set with input variable goal
        this.goal = new char[goal.length()];

        for (int i = 0; i < goal.length(); i++) {
            this.goal[i] = goal.charAt(i);
        }

        // Set number of parents here
        numberOfParents = 1200;
        // Set number of children here - REMARK: numberOfChildren mod numberOfParents must equal 0
        numberOfChildren = 1200;
        // Set crossover probability here
        crossoverProbability = 0.8d;
        // Set mutation probability (to mutate a single gene) here
        mutationProbability = 0.005d; //.005

        // Generates initial population with random values
        generatePopulation(lengthOfIndividual, 32, 122);
    }

    // Assigns fitness values to all Individuals
    @Override
    protected void assignFitness() {
        for (int i = 0; i < population.length; i++) {
            double individualFitness = 0;
            for (int j = 0; j < lengthOfIndividual; j++) {
                double specificFitness = 0;
                // Distance of a gene to the looked-for value
                int distance = goal[j] - population[i].chromosome[j];
                // if the distance is negative, turn it into a positive number
                if (distance < 0) {
                    distance *= -1;
                }
                // This gives letters of the Individual that have a distance of up to 4 to the looked-for letter a very high fitness (10 - distance * 2)
                if (distance < 5) {
                    specificFitness = 10 - distance * 2;
                }
                // This gives all other letters a lower value the further away they are
                if (distance > 5) {
                    specificFitness = 5.0 / distance;
                }
                individualFitness += specificFitness;
            }
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
                    population[i].mutateGeneWeasel(j, goal);
                }
            }
        }
    }

    // Testing method to see performance of the algorithm
    public static void main (String[] args) {
        //MAX CAPACITY W 5500 ITERATIONS, RUNTIME >1MIN
        /*Weasel weasel = new Weasel("And ten thousand peoploids split into small tribes, " +
        "coveting the highest of the sterile skyscrapers, " +
        "like packs of dogs assaulting the glass fronts of Love-Me Avenue.");
        */
        //LOW CAPACITY W 100 ITERATIONS, RUNTIME <3SEC
        Weasel weasel = new Weasel("And ten thousand peoploids split into small tribes");
        int j = 0;
        for (int i = 0; i < 6000 && weasel.getBest().getFitness() < weasel.goal.length*10; i++) {
            weasel.run();
            j++;
            // To see performance of algorithm during runtime for larger Strings
            System.out.println(weasel.getBest().getFitness());
        }
        System.out.println("Iteration number: " + j);
        System.out.println(weasel.getBest().toString());
        System.out.println(weasel.getBest().getFitness());
    }
}
