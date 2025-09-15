import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import javax.swing.*;


public class ExpenseCalculatorGUI extends JFrame{
    
    private java.util.List<JTextField> friendFields = new ArrayList<>();
    private java.util.List<ExpenseRow> expenseRows = new ArrayList<>();

    private JPanel friendsPanel;
    private JPanel expensesPanel;

    
    public ExpenseCalculatorGUI(){
        setTitle("Expense Calculator");
        setSize(900,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // === FRIENDS SECTION ===
        friendsPanel = new JPanel();
        friendsPanel.setLayout(new BoxLayout(friendsPanel,BoxLayout.Y_AXIS));
        friendsPanel.setBorder(BorderFactory.createTitledBorder("Friends"));

        JButton addFriendBtn = new JButton("Add friend!");
        addFriendBtn.addActionListener(e -> addFriendRow("Friend "));

        JPanel friendSection = new JPanel(new BorderLayout());
        friendSection.add(new JScrollPane(friendsPanel), BorderLayout.CENTER);
        friendSection.add(addFriendBtn, BorderLayout.SOUTH);

        // === EXPENSE SECTION ===
        expensesPanel = new JPanel();
        expensesPanel.setLayout(new BoxLayout(expensesPanel, BoxLayout.Y_AXIS));

        JButton addExpenseBtn = new JButton("Add Expense");
        addExpenseBtn.addActionListener(e -> addExpenseRow());

        JPanel expenseSection = new JPanel(new BorderLayout());
        expenseSection.add(new JScrollPane(expensesPanel), BorderLayout.CENTER);
        expenseSection.add(addExpenseBtn, BorderLayout.SOUTH);

        // Main layout
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topPanel.add(friendSection);
        topPanel.add(expenseSection);
        JPanel bottomPanel = new JPanel(new BorderLayout(5,5));
        // Split Panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        
        add(splitPane);
    }

    // adds to friend section
    private void addFriendRow(String defaultName){
        JTextField field = new JTextField(defaultName, 15);
        friendFields.add(field);  // add to field
        friendsPanel.add(field);  // add to panel 
        friendsPanel.revalidate(); // checks everything
        friendsPanel.repaint();  // exactly draws

        updateExpenseDropDowns();
    }

    // adds to expense section
    private void addExpenseRow(){
        ExpenseRow row = new ExpenseRow(friendFields);
        expenseRows.add(row);
        expensesPanel.add(row);
        expensesPanel.revalidate();
        expensesPanel.repaint();
    }

    private void updateExpenseDropDowns(){
        List<String> names = getFriendNames();
        for(ExpenseRow row : expenseRows){
            row.updateDropDown(names);
        }
    }

    private List<String> getFriendNames(){
        List<String> names = new ArrayList<>();
        for(JTextField f : friendFields){
            String name = f.getText().trim();
            if(!name.isEmpty()){
                names.add(name);
            }
        }
        return names;
    }



    private void calculateExpenses(){ // to be modified (can't pick up things from terminal anymore)
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of friends: ");
        int numFriends = sc.nextInt();
        sc.nextLine();

        // to do: pick up values from GUI

        List<String> friends = new ArrayList<>(); // empty arrayList (dynamic)
        for(int i = 0; i < numFriends; i++){
            System.out.print("Enter friend " + (i + 1) + " name: ");
            friends.add(sc.nextLine().trim());
        }

        System.out.print("Enter number of expense records: ");
        int numRecords = sc.nextInt();  // number of purchase entries (days)
        sc.nextLine();

        String[] payers = new String[numRecords];
        double[] amounts = new double[numRecords];

        for(int i = 0; i < numRecords; i++){
            System.out.print("Enter name of purchaser from day "+(i+1)+": ");
            payers[i] = sc.nextLine();
            
            System.out.print("Enter amount purchased from day " + (i+1) + ": ");
            amounts[i] = sc.nextDouble();
            sc.nextLine();
        }

        // everyone spends equal amounts
        // day 1
        // alice +120
        // bob -30
        // carol -30
        // dave -30
        // eve -30

        //day 2
        // alice +80   (-40)
        // bob +130      (+160)
        // carol -70    (-40)
        // dave -70     (-40)
        // eve -70      (-40)

        HashMap<String, Double> balances = new HashMap<>();

        for(String friend: friends){
            balances.put(friend, 0.0);
        }

        System.out.println(balances);

        for(int day = 0; day < numRecords; day++){
            // retrieve the total and payer of EACH day
            String payer = payers[day];
            double total = amounts[day];
            double share = total/friends.size();

            for(String friend:friends){
                if(friend.equals(payer)){
                    balances.put(friend, balances.get(friend) + total-share);

                }
                else{
                    balances.put(friend, balances.get(friend) - share);
                }
            }
        }
        System.out.println("Net balances: ");
        for(String friend:friends){
            System.out.println(friend + " " + balances.get(friend));
        }

        System.out.println("Simplified debts");
        simplifyDebts(balances);
    }

    static void simplifyDebts(HashMap <String, Double> balances){
        // finds largest creditor and smallest debtor
        // when  b.amount is first in (a,b) u r looking for the larger amount
        PriorityQueue <Person> creditors = new PriorityQueue<>((a,b) -> Double.compare(b.amount, a.amount));
        PriorityQueue<Person> debtors = new PriorityQueue<>((a,b) -> Double.compare(a.amount, b.amount));

        // entry -> each pair (key/value pair)
        // offer -> adds into priority queue

        // same thing as for(String name: names)
        for(HashMap.Entry<String, Double> entry: balances.entrySet()){
            String name = entry.getKey();
            double amount = entry.getValue();
            if(amount > 0){
                creditors.offer(new Person(name, amount));
            }

            else if(amount < 0){
                debtors.offer(new Person(name, -amount));
            }
        }
        while(!creditors.isEmpty() && !debtors.isEmpty()){
            Person creditor = creditors.poll();
            Person debtor = debtors.poll();

            double min = Math.min(creditor.amount, debtor.amount);
            System.out.printf("%s owes %s: $%.2f\n", debtor.name, creditor.name, min);

            // so if zero then u won't be readded into the hashmap
            if(creditor.amount > min){
                creditors.offer(new Person(creditor.name, creditor.amount - min));
            }
            if(debtor.amount > min){
                debtors.offer(new Person(debtor.name, debtor.amount - min));
            }
        }
            
    }

    // INNER CLASSES: (new types)

    // need new type bc it has to hold both string and double
    static class Person{
        String name;
        double amount;

        Person(String name, double amount){
            this.name = name;
            this.amount = amount;
        }
    }

    class ExpenseRow extends JPanel{
        JTextField description;
        JComboBox<String> payerDropDown;  // drop down menu
        JTextField amountField;

        ExpenseRow(java.util.List<JTextField> friends){
            setLayout(new FlowLayout(FlowLayout.LEFT));
            description = new JTextField("Expense");
            payerDropDown = new JComboBox<>();
            amountField = new JTextField(7);

            // label = piece of text
            add(new JLabel("Desc: "));
            add(description);
            add(new JLabel("Payer: "));
            add(payerDropDown);
            add(new JLabel("Amount: "));
            add(amountField);

            updateDropDown(getFriendNames());
        }

        // adds to dropdown as u go
        void updateDropDown(java.util.List<String> names){
            // clear out then add all again to update the list of friends
            
            payerDropDown.removeAllItems();
            for(String n : names){
                payerDropDown.addItem(n);
            }
        }
        // pick up the selected name from drop down menu
        String getPayer(){
            return (String)payerDropDown.getSelectedItem();
        }

        double getAmount(){
            try {
                // processes as a double
                return Double.parseDouble(amountField.getText().trim());
            } catch (Exception e) {
                return 0.0;
            }
        }
    }
    
}
