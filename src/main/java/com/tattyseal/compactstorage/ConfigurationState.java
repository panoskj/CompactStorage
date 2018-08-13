package com.tattyseal.compactstorage;

// This is supposed to be sent from server to clients logging in.
public class ConfigurationState
{
	public String storage;
    public String storageBackpack;

    public String[] primary;
    public String[] secondary;

    public String binder;
    public String binderBackpack;

    public float storageModifier;
    public float primaryModifier;
    public float secondaryModifier;
    public float binderModifier;

    public boolean shouldConnect;
	
    public int capacityBarrel;
    public int capacityDrum;
}