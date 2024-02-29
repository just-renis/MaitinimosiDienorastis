package com.example.maitinimosidienorastis;

import java.util.Map;

public class Product {
    private String key, name, edibleCoefficient, water, dryMatter, proteinTotal, proteinAnimal, proteinPlant, fatTotal,
            fattyAcidsTotalSaturated, fattyAcidsTotalMonounsaturated, fattyAcidsTotalPolyunsaturated, cholesterol, carbohydrates,
            starch, sugars, fibre, ashMinerals, sodium, magnesium, phosphorus, potassium, calcium, iron, zinc, selenium, iodide,
            vitaminA_RE, vitaminD, vitaminE_TE, thiamin, riboflavin, niacin, niacinEquivalent, folicAcid, vitaminB6, vitaminB12,
            vitaminC, alcohol, energyValueKJ, energyValueKCAL;
    private boolean isMineNeededToShow;
    public Product(String name, String key, boolean isMineNeededToShow){
        this.name = name;
        this.key = key;
        this.isMineNeededToShow = isMineNeededToShow;
    }
    public Product(Map<String, String> fieldValues) {
        this.name = fieldValues.get("name");
        this.edibleCoefficient = fieldValues.get("edibleCoefficient");
        this.water = fieldValues.get("water");
        this.dryMatter = fieldValues.get("dryMatter");
        this.proteinTotal = fieldValues.get("proteinTotal");
        this.proteinAnimal = fieldValues.get("proteinAnimal");
        this.proteinPlant = fieldValues.get("proteinPlant");
        this.fatTotal = fieldValues.get("fatTotal");
        this.fattyAcidsTotalSaturated = fieldValues.get("fattyAcidsTotalSaturated");
        this.fattyAcidsTotalMonounsaturated = fieldValues.get("fattyAcidsTotalMonounsaturated");
        this.fattyAcidsTotalPolyunsaturated = fieldValues.get("fattyAcidsTotalPolyunsaturated");
        this.cholesterol = fieldValues.get("cholesterol");
        this.carbohydrates = fieldValues.get("carbohydrates");
        this.starch = fieldValues.get("starch");
        this.sugars = fieldValues.get("sugars");
        this.fibre = fieldValues.get("fibre");
        this.ashMinerals = fieldValues.get("ashMinerals");
        this.sodium = fieldValues.get("sodium");
        this.magnesium = fieldValues.get("magnesium");
        this.phosphorus = fieldValues.get("phosphorus");
        this.potassium = fieldValues.get("potassium");
        this.calcium = fieldValues.get("calcium");
        this.iron = fieldValues.get("iron");
        this.zinc = fieldValues.get("zinc");
        this.selenium = fieldValues.get("selenium");
        this.iodide = fieldValues.get("iodide");
        this.vitaminA_RE = fieldValues.get("vitaminA_RE");
        this.vitaminD = fieldValues.get("vitaminD");
        this.vitaminE_TE = fieldValues.get("vitaminE_TE");
        this.thiamin = fieldValues.get("thiamin");
        this.riboflavin = fieldValues.get("riboflavin");
        this.niacin = fieldValues.get("niacin");
        this.niacinEquivalent = fieldValues.get("niacinEquivalent");
        this.folicAcid = fieldValues.get("folicAcid");
        this.vitaminB6 = fieldValues.get("vitaminB6");
        this.vitaminB12 = fieldValues.get("vitaminB12");
        this.vitaminC = fieldValues.get("vitaminC");
        this.alcohol = fieldValues.get("alcohol");
        this.energyValueKJ = fieldValues.get("energyValueKJ");
        this.energyValueKCAL = fieldValues.get("energyValueKCAL");
        this.isMineNeededToShow = false;
    }
    public String getName() { return this.name; }
    public String getKey() { return this.key; }
    public String getEdibleCoefficient() { return this.edibleCoefficient; }
    public String getWater() { return this.water; }
    public String getDryMatter() { return this.dryMatter; }
    public String getProteinTotal() { return this.proteinTotal; }
    public String getProteinAnimal() { return this.proteinAnimal; }
    public String getProteinPlant() { return this.proteinPlant; }
    public String getFatTotal() { return this.fatTotal; }
    public String getFattyAcidsTotalSaturated() { return this.fattyAcidsTotalSaturated; }
    public String getFattyAcidsTotalMonounsaturated() { return this.fattyAcidsTotalMonounsaturated; }
    public String getFattyAcidsTotalPolyunsaturated() { return this.fattyAcidsTotalPolyunsaturated; }
    public String getCholesterol() { return this.cholesterol; }
    public String getCarbohydrates() { return this.carbohydrates; }
    public String getStarch() { return this.starch; }
    public String getSugars() { return this.sugars; }
    public String getFibre() { return this.fibre; }
    public String getAshMinerals() { return this.ashMinerals; }
    public String getSodium() { return this.sodium; }
    public String getMagnesium() { return this.magnesium; }
    public String getPhosphorus() { return this.phosphorus; }
    public String getPotassium() { return this.potassium; }
    public String getCalcium() { return this.calcium; }
    public String getIron() { return this.iron; }
    public String getZinc() { return this.zinc; }
    public String getSelenium() { return this.selenium; }
    public String getIodide() { return this.iodide; }
    public String getVitaminA_RE() { return this.vitaminA_RE; }
    public String getVitaminD() { return this.vitaminD; }
    public String getVitaminE_TE() { return this.vitaminE_TE; }
    public String getThiamin() { return this.thiamin; }
    public String getRiboflavin() { return this.riboflavin; }
    public String getNiacin() { return this.niacin; }
    public String getNiacinEquivalent() { return this.niacinEquivalent; }
    public String getFolicAcid() { return this.folicAcid; }
    public String getVitaminB6() { return this.vitaminB6; }
    public String getVitaminB12() { return this.vitaminB12; }
    public String getVitaminC() { return this.vitaminC; }
    public String getAlcohol() { return this.alcohol; }
    public String getEnergyValueKJ() { return this.energyValueKJ; }
    public String getEnergyValueKCAL() { return this.energyValueKCAL; }
    public boolean getIsMineNeededToShow() { return this.isMineNeededToShow; }
}
