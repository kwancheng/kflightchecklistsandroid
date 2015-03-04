package com.acproma.kflightchecklists.data;

/**
 * Created by kwan.cheng on 9/11/2014.
 */
public class Column {
    private String name = null;
    private String type = null;
    private String modifier = null;

    public Column(String name, String type, String modifier) {
        this.name = name;
        this.type = type;
        this.modifier = modifier;
    }

    static public Column createStringColumn(String name) {
        return new Column(name, "text", "not null");
    }

    static public Column createBooleanColumn(String name) {
        return new Column(name, "integer", "not null");
    }

    static public Column createIntegerColumn(String name) {
        return new Column(name, "integer", "not null");
    }

    static public Column createIdColumn(String name) {
        return new Column(name, "integer", "primary key autoincrement");
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getModifier() { return modifier; }

    public String getCreateStatement() {
        return String.format("%s %s %s", name, type, modifier);
    }
}
