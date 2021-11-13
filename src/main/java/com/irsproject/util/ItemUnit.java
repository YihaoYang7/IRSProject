package com.irsproject.util;

import java.util.Arrays;
import java.util.LinkedList;

public class ItemUnit
{
    private LinkedList<String> authors;
    private LinkedList<String> ee;
    private String title;
    private String[] date;

    public ItemUnit()
    {
        this.authors = new LinkedList<>();
        this.ee = new LinkedList<>();
        this.title = null;
        this.date = new String[2];
    }

    public LinkedList<String> getAuthors()
    {
        return authors;
    }

    public LinkedList<String> getEe()
    {
        return ee;
    }

    public String getTitle()
    {
        return title;
    }

    public String[] getDate()
    {
        return date;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setDate(String date, int index)
    {
        this.date[index] = date;
    }

    public void print()
    {
        System.out.println("Title: " + title);
        System.out.print("Author: ");
        for (String name : authors)
        {
            System.out.print(name + "\t");
        }
        System.out.println();
        for (String link : ee)
        {
            System.out.println(link);
        }
        System.out.println("Date: " + this.date[0] + " " + this.date[1]);
    }
}
