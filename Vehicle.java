
public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;
    
    private String capitalize(String input) {
    	if (input == null || input.isEmpty()) {
    		return input;
    	}
    	return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public enum VehicleStatus { Available, Held, Rented, UnderMaintenance, OutOfService }

    public Vehicle(String licensePlate, String make, String model, int year) {
    	setLicensePlate(licensePlate);
    	this.make = capitalize(make);
    	this.model = capitalize(model);
        this.year = year;
        this.status = VehicleStatus.Available;
    }

    //public Vehicle() {
        //this(null, null, 0);
    //}

    public void setLicensePlate(String plate) {
    	if(!validPlate(plate)) {
    		throw new IllegalArgumentException("Invalid license plate");
    	}
        this.licensePlate = plate == null ? null : plate.toUpperCase();
    }

    public void setStatus(VehicleStatus status) {
    	this.status = status;
    }

    public String getLicensePlate() { return licensePlate; }

    public String getMake() { return make; }

    public String getModel() { return model;}

    public int getYear() { return year; }

    public VehicleStatus getStatus() { return status; }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }

    private boolean validPlate(String plate) {
    	if(plate == null || plate.isEmpty()) {
    		return false;
    	}
    	
    	return plate.matches("^[A-Z]{3}[0-9]{3}$");
    }
}