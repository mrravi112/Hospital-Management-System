package managers;

import models.Bill;
import java.util.ArrayList;
import java.io.*;

public class BillingManager {

    private static final String FILE = "data/bills.txt";
    private ArrayList<Bill> list     = new ArrayList<>();
    private int idCounter            = 1;

    public BillingManager() {
        new File("data").mkdirs();
        loadFromFile();
        idCounter = list.size() + 1;
    }

    private String nextId() { return "B" + String.format("%03d", idCounter); }

    public Bill createBill(String patientId, String patientName,
                           double doctorFee, double medicineCost,
                           double roomCharges, String date) {
        Bill b = new Bill(nextId(), patientId, patientName,
                          doctorFee, medicineCost, roomCharges, date);
        idCounter++;
        list.add(b);
        saveToFile();
        return b;
    }

    public ArrayList<Bill> getAll()  { return list; }
    public int count()               { return list.size(); }

    public Bill findById(String id) {
        for (Bill b : list) if (b.getBillId().equalsIgnoreCase(id)) return b;
        return null;
    }

    public void markPaid(String id) {
        Bill b = findById(id);
        if (b != null) { b.setStatus("Paid"); saveToFile(); }
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE, false))) {
            for (Bill b : list) { bw.write(b.toFileString()); bw.newLine(); }
        } catch (IOException e) { System.out.println("Save error: " + e.getMessage()); }
    }

    public void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim(); if (line.isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length == 9) {
                    Bill b = new Bill(p[0], p[1], p[2],
                                      Double.parseDouble(p[3]), Double.parseDouble(p[4]),
                                      Double.parseDouble(p[5]), p[7]);
                    b.setStatus(p[8]);
                    list.add(b);
                }
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) { System.out.println("Load error: " + e.getMessage()); }
    }
}
