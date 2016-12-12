package app.view;


public class View {
    public static class Rest {}
    public static class Output extends Rest {}
    public static class Body extends Rest {}
    public enum ViewType {
        Rest, Body, Output
    }
}
