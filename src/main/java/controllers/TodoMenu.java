package controllers;

import javax.swing.*;

public class TodoMenu {

    private final TodoController todoController = new TodoController();

    public void start() {
        String userChoice = JOptionPane.showInputDialog(this.getMenuItems());
        this.handleUserChoice(userChoice);
        // can be here as well this.start() - recursion
    }

    private String getMenuItems() {
        return """
                Welcome to Todo Application
                1. Add Todo\s
                2. Display Todo List\s
                3. View Single Item\s
                4. Remove Todo Item\s
                5. Update Todo Item\s
                6. Exit""";
    }

    private void handleUserChoice(String userChoice) {
        switch (userChoice) {
            case "1":
               todoController.addTodo();
                break;
            case "2":
                todoController.viewAllTodo();
                break;
            case "3":
               todoController.viewTodo();
                break;
            case "4":
                todoController.removeTodo();
                break;
            case "5":
              todoController.updateTodo();
                break;
            case "6":
                System.exit(0);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Please choose an option from a list.");
                break;
        }
        this.start();
    }
}
