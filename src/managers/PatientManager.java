package managers;

import models.Patient;
import java.util.ArrayList;
import java.io.*;


public class PatientManager {

    private static final String FILE = "data/patients.txt";
    private ArrayList<Patient> list  = new ArrayList<>();
    private int idCounter            = 1;

    public PatientManager() {
        new File("data").mkdirs(); 
        loadFromFile();
        idCounter = list.size() + 1;
    }

    private String nextId() { return "P" + String.format("%03d", idCounter); }

    public void addPatient(Patient p) {
        idCounter++;
        list.add(p);
        saveToFile();
    }

    public Patient createPatient(String name, int age, String gender,
                                 String disease, String contact, String status) {
        Patient p = new Patient(nextId(), name, age, gender, disease, contact, status);
        addPatient(p);
        return p;
    }

    public ArrayList<Patient> getAll() { return list; }

    public Patient findById(String id) {
        for (Patient p : list)
            if (p.getPatientId().equalsIgnoreCase(id)) return p;
        return null;
    }

    public boolean deleteById(String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPatientId().equalsIgnoreCase(id)) {
                list.remove(i);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public void update(Patient p) { saveToFile(); }

    public int count() { return list.size(); }


    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE, false))) {
            for (Patient p : list) { bw.write(p.toFileString()); bw.newLine(); }
        } catch (IOException e) { System.out.println("Save error: " + e.getMessage()); }
    }

    public void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length == 7)
                    list.add(new Patient(p[0], p[1], Integer.parseInt(p[2]),
                                         p[3], p[4], p[5], p[6]));
            }
        } catch (FileNotFoundException e) {
            // Normal on first run
        } catch (IOException e) { System.out.println("Load error: " + e.getMessage()); }
    }
}
