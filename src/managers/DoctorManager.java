package managers;

import models.Doctor;
import java.util.ArrayList;
import java.io.*;


public class DoctorManager {

    private static final String FILE = "data/doctors.txt";
    private ArrayList<Doctor> list   = new ArrayList<>();
    private int idCounter            = 1;

    public DoctorManager() {
        new File("data").mkdirs();
        loadFromFile();
        idCounter = list.size() + 1;
    }

    private String nextId() { return "D" + String.format("%03d", idCounter); }

    public Doctor createDoctor(String name, String specialization,
                               String contact, double fee, String availability) {
        Doctor d = new Doctor(nextId(), name, specialization, contact, fee, availability);
        idCounter++;
        list.add(d);
        saveToFile();
        return d;
    }

    public void addDoctor(Doctor d) { list.add(d); idCounter++; saveToFile(); }

    public ArrayList<Doctor> getAll() { return list; }

    public Doctor findById(String id) {
        for (Doctor d : list)
            if (d.getDoctorId().equalsIgnoreCase(id)) return d;
        return null;
    }

    public boolean deleteById(String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDoctorId().equalsIgnoreCase(id)) {
                list.remove(i); saveToFile(); return true;
            }
        }
        return false;
    }

    public void update(Doctor d) { saveToFile(); }

    public int count() { return list.size(); }

    public int countOnDuty() {
        int n = 0;
        for (Doctor d : list) if ("On Duty".equals(d.getAvailability())) n++;
        return n;
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE, false))) {
            for (Doctor d : list) { bw.write(d.toFileString()); bw.newLine(); }
        } catch (IOException e) { System.out.println("Save error: " + e.getMessage()); }
    }

    public void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim(); if (line.isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length == 6)
                    list.add(new Doctor(p[0], p[1], p[2], p[3],
                                        Double.parseDouble(p[4]), p[5]));
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) { System.out.println("Load error: " + e.getMessage()); }
    }
}
