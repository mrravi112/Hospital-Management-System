package models;


public class Doctor {

    private String doctorId;
    private String name;
    private String specialization;
    private String contactNumber;
    private double consultationFee;
    private String availability; 

    public Doctor(String doctorId, String name, String specialization,
                  String contactNumber, double consultationFee, String availability) {
        this.doctorId        = doctorId;
        this.name            = name;
        this.specialization  = specialization;
        this.contactNumber   = contactNumber;
        this.consultationFee = consultationFee;
        this.availability    = availability;
    }

    
    public String getDoctorId()        { return doctorId; }
    public String getName()            { return name; }
    public String getSpecialization()  { return specialization; }
    public String getContactNumber()   { return contactNumber; }
    public double getConsultationFee() { return consultationFee; }
    public String getAvailability()    { return availability; }

   
    public void setName(String name)                     { this.name = name; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setContactNumber(String contactNumber)   { this.contactNumber = contactNumber; }
    public void setConsultationFee(double fee)           { this.consultationFee = fee; }
    public void setAvailability(String availability)     { this.availability = availability; }

    public String toFileString() {
        return doctorId + "," + name + "," + specialization + "," +
               contactNumber + "," + consultationFee + "," + availability;
    }

    @Override
    public String toString() { return doctorId + " - Dr. " + name + " (" + specialization + ")"; }
}
