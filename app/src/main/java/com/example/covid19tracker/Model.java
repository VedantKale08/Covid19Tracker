package com.example.covid19tracker;

public class Model {
    String State;
    String cases,recovered,deaths;

    public Model(){

    }

    public Model(String state, String cases, String recovered, String deaths) {
        State = state;
        this.cases = cases;
        this.recovered = recovered;
        this.deaths = deaths;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCases() {
        return cases;
    }

    public void setCases(String cases) {
        this.cases = cases;
    }

    public String getRecovered() {
        return recovered;
    }

    public void setRecovered(String recovered) {
        this.recovered = recovered;
    }

    public String getDeaths() {
        return deaths;
    }

    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }
}
