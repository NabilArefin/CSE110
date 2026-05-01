import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

abstract class Expense {
    private double amount;
    private String date;
    private String desc;
    private String cat;

    Expense(double amount, String date, String desc, String cat) {
        this.amount = amount;
        this.date = date;
        this.desc = desc;
        this.cat = cat;
    }

    double getAmount() { return amount; }
    String getDate() { return date; }
    String getDescription() { return desc; }
    String getCategory() { return cat; }
    void setAmount(double amount) { this.amount = amount; }

    abstract double calculateAmount();

    @Override
    public String toString() {
        return cat + " | " + amount + " | " + date + " | " + desc;
    }
}

class FoodE extends Expense {
    FoodE(double a, String d, String ds) { super(a, d, ds, "Food"); }
    double calculateAmount() { return getAmount(); }
}

class TravelE extends Expense {
    TravelE(double a, String d, String ds) { super(a, d, ds, "Travel"); }
    double calculateAmount() { return getAmount(); }
}

class EntertainmentE extends Expense {
    EntertainmentE(double a, String d, String ds) { super(a, d, ds, "Entertainment"); }
    double calculateAmount() { return getAmount(); }
}

class HealthE extends Expense {
    HealthE(double a, String d, String ds) { super(a, d, ds, "Health"); }
    double calculateAmount() { return getAmount(); }
}

class ShoppingE extends Expense {
    ShoppingE(double a, String d, String ds) { super(a, d, ds, "Shopping"); }
    double calculateAmount() { return getAmount(); }
}

class EducationE extends Expense {
    EducationE(double a, String d, String ds) { super(a, d, ds, "Education"); }
    double calculateAmount() { return getAmount(); }
}

class OthersE extends Expense {
    OthersE(double a, String d, String ds) { super(a, d, ds, "Others"); }
    double calculateAmount() { return getAmount(); }
}

class InvalidExpenseException extends Exception {
    InvalidExpenseException(String msg) { super(msg); }
}
class ExpenseManager {
    private ArrayList<Expense> expenses = new ArrayList<>();
    private double budget = 0;

    void addExpense(Expense e) { expenses.add(e); }

    String viewExpenses() throws Exception {
        if (expenses.isEmpty()) throw new Exception("No expenses added yet");
        StringBuilder sb = new StringBuilder("--- ALL EXPENSES ---\n");
        int i = 1;
        for (Expense e : expenses) {
            sb.append(i).append(". ").append(e).append("\n");
            i++;
        }
        return sb.toString();
    }

    double totalExpense() {
        double total = 0;
        for (Expense e : expenses) {
            total += e.calculateAmount();
        }
        return total;
    }

    String filter(String cat) throws Exception {
        StringBuilder sb = new StringBuilder("Results for: " + cat + "\n");
        boolean found = false;
        for (Expense e : expenses) {
            if (e.getCategory().equalsIgnoreCase(cat)) {
                sb.append(e).append("\n");
                found = true;
            }
        }
        if (!found) throw new Exception("No data found");
        return sb.toString();
    }

    void delete(int i) throws Exception {
        if (i < 1 || i > expenses.size()) throw new Exception("Invalid index!");
        expenses.remove(i - 1);
    }

    String search(String date) throws Exception {
        StringBuilder sb = new StringBuilder("Results for: " + date + "\n");
        boolean found = false;
        for (Expense e : expenses) {
            if (e.getDate().equals(date)) {
                sb.append(e).append("\n");
                found = true;
            }
        }
        if (!found) throw new Exception("No expense on this date");
        return sb.toString();
    }

    String categoryTotal() {
        HashMap<String, Double> map = new HashMap<>();
        for (Expense e : expenses) {
            map.put(e.getCategory(), map.getOrDefault(e.getCategory(), 0.0) + e.calculateAmount());
        }
        StringBuilder sb = new StringBuilder("--- CATEGORY TOTALS ---\n");
        for (String key : map.keySet()) {
            sb.append(key).append(": ").append(map.get(key)).append("\n");
        }
        return sb.toString();
    }

    void setBudget(double budget) { this.budget = budget; }

    String checkBudget() {
        double total = totalExpense();
        if (total > budget)
            return "Budget exceeded! Spent: " + total + " / Budget: " + budget;
        else
            return "Within budget. Remaining: " + (budget - total);
    }

    void editExpense(int index, double newA) throws Exception {
        if (index < 1 || index > expenses.size()) throw new Exception("Invalid index");
        expenses.get(index - 1).setAmount(newA);
    }

    String exportSummary() {
        try {
            FileWriter f = new FileWriter("summary.txt");
            StringBuilder sb = new StringBuilder("\n--- EXPENSE RECEIPT ---\n");
            f.write("--- EXPENSE RECEIPT ---\n");
            double total = 0;
            int i = 1;
            for (Expense e : expenses) {
                double finalA = e.calculateAmount();
                total += finalA;
                String line = i + ". " + e.getCategory() + " | " + e.getAmount() + " | " + finalA;
                sb.append(line).append("\n");
                f.write(line + "\n");
                i++;
            }
            String totalLine = "Total: " + total;
            sb.append(totalLine).append("\n(Saved to summary.txt)");
            f.write(totalLine + "\n");
            f.close();
            return sb.toString();
        } catch (Exception e) {
            return "Error exporting file.";
        }
    }
}

public class M extends JFrame {
    private ExpenseManager m = new ExpenseManager();
    private JTextArea displayArea;
    private JTextField txtAmt, txtDate, txtDesc, txtBudget, txtInput;
    private JComboBox<String> comboCat;

    public M() {
        setTitle("Student Expense Tracker");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Amount:"));
        txtAmt = new JTextField();
        inputPanel.add(txtAmt);

        inputPanel.add(new JLabel("Date:"));
        txtDate = new JTextField();
        inputPanel.add(txtDate);

        inputPanel.add(new JLabel("Description:"));
        txtDesc = new JTextField();
        inputPanel.add(txtDesc);

        inputPanel.add(new JLabel("Category:"));
        String[] cats = {"Food", "Travel", "Entertainment", "Health", "Shopping", "Education", "Others"};
        comboCat = new JComboBox<>(cats);
        inputPanel.add(comboCat);

        JButton btnAdd = new JButton("Add Expense");
        inputPanel.add(btnAdd);

        JButton btnView = new JButton("View All");
        inputPanel.add(btnView);

        add(inputPanel, BorderLayout.NORTH);

        displayArea = new JTextArea();
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(displayArea), BorderLayout.CENTER);
        JPanel sidePanel = new JPanel(new GridLayout(8, 1, 5, 5));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton btnTotal = new JButton("Total");
        JButton btnFilter = new JButton("Filter by catagory");
        JButton btnSearch = new JButton("Search Date");
        JButton btnCatTotal = new JButton("Catagory Totals");
        JButton btnSetBudget = new JButton("Set Budget");
        JButton btnCheckBudget = new JButton("Check Budget");
        JButton btnEdit = new JButton("Edit (by index)");
        JButton btnExport = new JButton("Export");

        sidePanel.add(btnTotal);
        sidePanel.add(btnFilter);
        sidePanel.add(btnSearch);
        sidePanel.add(btnCatTotal);
        sidePanel.add(btnSetBudget);
        sidePanel.add(btnCheckBudget);
        sidePanel.add(btnEdit);
        sidePanel.add(btnExport);
        add(sidePanel, BorderLayout.EAST);

        btnAdd.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(txtAmt.getText());
                String date = txtDate.getText();
                String desc = txtDesc.getText();
                String cat = (String) comboCat.getSelectedItem();
                Expense ex;
                if (cat.equals("Food")) ex = new FoodE(amt, date, desc);
                else if (cat.equals("Travel")) ex = new TravelE(amt, date, desc);
                else if (cat.equals("Entertainment")) ex = new EntertainmentE(amt, date, desc);
                else if (cat.equals("Health")) ex = new HealthE(amt, date, desc);
                else if (cat.equals("Shopping")) ex = new ShoppingE(amt, date, desc);
                else if (cat.equals("Education")) ex = new EducationE(amt, date, desc);
                else ex = new OthersE(amt, date, desc);

                m.addExpense(ex);
                displayArea.setText("Expense Added: " + ex);
            } catch (Exception ex) {
                displayArea.setText("Error: Check your input values!");
            }
        });

        btnView.addActionListener(e -> {
            try { displayArea.setText(m.viewExpenses()); }
            catch (Exception ex) { displayArea.setText(ex.getMessage()); }
        });

        btnTotal.addActionListener(e -> displayArea.setText("Total Expenses: " + m.totalExpense()));

        btnFilter.addActionListener(e -> {
            String cat = JOptionPane.showInputDialog("Enter Category to filter:");
            try { displayArea.setText(m.filter(cat)); }
            catch (Exception ex) { displayArea.setText(ex.getMessage()); }
        });

        btnSearch.addActionListener(e -> {
            String d = JOptionPane.showInputDialog("Enter Date:");
            try { displayArea.setText(m.search(d)); }
            catch (Exception ex) { displayArea.setText(ex.getMessage()); }
        });

        btnCatTotal.addActionListener(e -> displayArea.setText(m.categoryTotal()));

        btnSetBudget.addActionListener(e -> {
            String bStr = JOptionPane.showInputDialog("Enter Budget:");
            if (bStr != null) {
                m.setBudget(Double.parseDouble(bStr));
                displayArea.setText("Budget set to: " + bStr);
            }
        });

        btnCheckBudget.addActionListener(e -> displayArea.setText(m.checkBudget()));

        btnEdit.addActionListener(e -> {
            try {
                int idx = Integer.parseInt(JOptionPane.showInputDialog("Enter Index to edit:"));
                double newAmt = Double.parseDouble(JOptionPane.showInputDialog("Enter New Amount:"));
                m.editExpense(idx, newAmt);
                displayArea.setText("Index " + idx + " updated.");
            } catch (Exception ex) {
                displayArea.setText("Error: " + ex.getMessage());
            }
        });

        btnExport.addActionListener(e -> displayArea.setText(m.exportSummary()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new M().setVisible(true));
    }
}