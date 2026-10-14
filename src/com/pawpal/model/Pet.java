
package com.pawpal.model;

public class Pet {

    private int petId;
    private int ownerId;
    private String petName;
    private String species;
    private String breed;
    private int age;

    public Pet(int petId, int ownerId, String petName,
               String species, String breed, int age) {

        this.petId = petId;
        this.ownerId = ownerId;
        this.petName = petName;
        this.species = species;
        this.breed = breed;
        this.age = age;
    }

    public int getPetId() {
        return petId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getPetName() {
        return petName;
    }

    public String getSpecies() {
        return species;
    }

    public String getBreed() {
        return breed;
    }

    public int getAge() {
        return age;
    }
}
