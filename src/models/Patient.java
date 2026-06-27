package models;


public class Patient {

    private String patientId;
    private String name;
    private int    age;
    private String gender;
    private String disease;
    private String contactNumber;
    private String status; 

    public Patient(String patientId, String name, int age,
                   String gender, String disease, String contactNumber, String status) {
        this.patientId     = patientId;
        this.name          = name;
        this.age           = age;
        this.gender        = gender;
        this.disease       = disease;
        this.contactNumber = contactNumber;
        this.status        = status;
    }

    
    public String getPatientId()     { return patientId; }
    public String getName()          { return name; }
    public int    getAge()           { return age; }
    public String getGender()        { return gender; }
    public String getDisease()       { return disease; }
    public String getContactNumber() { return contactNumber; }
    public String getStatus()        { return status; }

    public void setName(String name)                   { this.name = name; }
    public void setAge(int age)                        { this.age = age; }
    public void setGender(String gender)               { this.gender = gender; }
    public void setDisease(String disease)             { this.disease = disease; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setStatus(String status)               { this.status = status; }

    
    public String toFileString() {
        return patientId + "," + name + "," + age + "," +
               gender + "," + disease + "," + contactNumber + "," + status;
    }

    @Override
    public String toString() { return patientId + " - " + name; }
}
