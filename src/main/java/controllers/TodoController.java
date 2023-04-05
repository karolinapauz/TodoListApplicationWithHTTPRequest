package controllers;

import entity.Priority;
import entity.Todo;
import entity.TodoStatus;
import org.apache.commons.lang.enums.EnumUtils;
import services.TodoService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class TodoController {

    private final TodoService todoService = new TodoService();

    public void addTodo() {
        try {
            Todo todo = this.collectTodoInfo();
            this.todoService.createTodo(todo);
            this.displayMessage("Todo created successfully!");
        } catch (Exception exception) {
            displayMessage(exception.getMessage());
        }
    }

    private void displayMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    private Todo collectTodoInfo() {
        Todo todo = new Todo();
        todo.setDescription(this.getUserInput("What would you like to do? "));
        todo.setDueDate((this.getUserInput("What is the task due date (2022-01-02)")));
        todo.setOwner(this.getUserInput("Who owns this item? "));

        List enumList = EnumUtils.getEnumList(Priority.class);
        //String [] prioritiesArray = {"LOW", "MEDIUM", "HIGH", "VERY HIGH"};

        todo.setStatus(TodoStatus.valueOf(this.getUserInput("What is the status? " +
                "(PENDING, IN PROGRESS, COMPLETED, DELETED)")));
        todo.setPriority(
                Priority.valueOf(this.getUserInputFromDropDown(
                        enumList.toArray(),
                       "Choose priority: ",
                       "How important is this item? ").toString()
                )
        );
        return todo;
    }

    private String getUserInput(String info) {
        return JOptionPane.showInputDialog(info);
    }

    private Object getUserInputFromDropDown(Object[] dropdownOptions, String title, String message) {
       return JOptionPane.showInputDialog(
                null,
                message,
                title,
                JOptionPane.QUESTION_MESSAGE,
                null,
                dropdownOptions,
                dropdownOptions[0]
        );
    }

    public void viewAllTodo() {
        try {
            StringBuilder todoListAsString = new StringBuilder();

            for (Todo todo : this.todoService.getAllTodoItems()) {
                todoListAsString.append(todo.toString());
            }
            this.displayMessage(todoListAsString.toString());
        } catch (Exception exception) {
            this.displayMessage(exception.getMessage());
        }
    }

    public void viewTodo() {
        try {
            String todoId = this.getUserInput("Enter the id of todo item");
            Todo todo = this.todoService.getTodoItem(todoId);

            this.displayMessage(new StringBuilder()
                    .append("Description: ").append(todo.getDescription()).append("\n")
                    .append("Status: ").append(todo.getStatus()).append("\n")
                    .append("Owner: ").append(todo.getOwner()).append("\n")
                    .append("Priority: ").append(todo.getPriority()).append("\n")
                    .append("Due date: ").append(todo.getDueDate()).append("\n")
                    .toString());
        } catch (Exception exception) {
            this.displayMessage(exception.getMessage());
        }

    }

    public void removeTodo() {
        try {
            List<Todo> existingTodoItems = this.todoService.getAllTodoItems();
            this.getUserInputFromDropDown(
                    existingTodoItems.toArray(),
                    "Delete item ",
                    "choose the item to delete"
            );

            String todoId = this.getUserInput("Enter the id of todo item to remove ");
            this.todoService.deleteTodoItem(todoId);

            this.displayMessage("Todo item deleted successfully");
        } catch (Exception exception) {
            this.displayMessage(exception.getMessage());
        }
    }

    public void updateTodo() {
        try {
            String todoId = this.getUserInput("Enter the id of todo item to update");
            Todo todo = this.collectTodoInfo();
            this.todoService.updateTodoItem(todo, todoId);
            this.displayMessage("Todo updated successfully!");
        } catch (Exception exception) {
            displayMessage(exception.getMessage());
        }
    }

}
