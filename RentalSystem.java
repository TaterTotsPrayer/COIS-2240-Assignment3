import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;

public class RentalSystem {
	//loadData();
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();
    private static RentalSystem instance;
    
    private RentalSystem() {
    	loadData();
    }
    
    public static RentalSystem getInstance() {
    	if (instance == null) {
    		instance = new RentalSystem();import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;

public class RentalSystem {
	//loadData();
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();
    private static RentalSystem instance;
    
    private RentalSystem() {
    	loadData();
    }
    
    public static RentalSystem getInstance() {
    	if (instance == null) {
    		instance = new RentalSystem();
    	}
    	return instance;
    }
    private void saveVehicle(Vehicle v) {
    	try (FileWriter writer = new FileWriter("vehicle.txt", true)){
    		String type;
    		String extra = "";
    		
    		if (v instanceof Car) {
    			type = "Car";
    			Car c = (Car) v;
    			extra = String.valueOf(c.getNumSeats());
    		}
    		else if (v instanceof Minibus) {
    			type = "Minibus";
    			Minibus m = (Minibus) v;
    			extra = String.valueOf(m.getInfo().contains("Yes"));
    		}
    		else if (v instanceof PickupTruck) {
    			type = "PickupTruck";
    			PickupTruck p = (PickupTruck) v;
    			extra = p.getCargoSize() + "|" + p.hasTrailer();
    		}
    		else {
    			type = "unknown";
    		}
    		writer.write(v.getLicensePlate() + "," + v.getMake() + "," + v.getModel() + "," + v.getYear() + "," + type + "," + extra + "\n");
    		
    	}
    	catch (IOException e) {
    		System.out.println("Error saving vehicle: " + e.getMessage());
    	}
    }
    private void saveCustomer(Customer c) {
    	try (FileWriter writer = new FileWriter("customers.txt", true)){
    		writer.write(c.getCustomerId() + "," + c.getCustomerName() + "\n");
    		
    	}
    	catch (IOException e) {
    		System.out.println("Error saving customer: " + e.getMessage());
    	}
    }
    private void saveRecord(RentalRecord r) {
    	try (FileWriter writer = new FileWriter("rental_records.txt", true)){
    		writer.write(r.getRecordType() + "," + r.getVehicle().getLicensePlate() + "," + r.getCustomer().getCustomerName() + "," + r.getRecordDate().toString() + "," + r.getTotalAmount() + "\n");
    		
    	}
    	catch (IOException e) {
    		System.out.println("Error saving rental records: " + e.getMessage());
    	}
    }
    private void loadData() {
    	try (BufferedReader br = new BufferedReader(new FileReader("vehicle.txt"))){
    		String line;
    		while ((line = br.readLine()) != null) {
    			String[] parts = line.split(",");
    			if (parts.length < 6) continue;
    			String plate = parts[0];
    			String make = parts[1];
    			String model = parts[2];
    			int year = Integer.parseInt(parts[3]);
    			String type = parts[4];
    			String extra = parts[5];
    			
    			Vehicle v = null;
    			switch (type) {
    			case "Car": 
    				v = new Car(plate, make, model, year, Integer.parseInt(extra));
    			break;
    			case "Minibus": 
    				v = new Minibus(plate, make, model, year, Boolean.parseBoolean(extra));
    			break;
    			case "PickupTruck": 
    				String[] pickupExtra = extra.split("\\|");
    				double cargoSize = Double.parseDouble(pickupExtra[0]);
    				boolean hasTrailer = Boolean.parseBoolean(pickupExtra[1]);
    				v = new PickupTruck(plate, make, model, year, cargoSize, hasTrailer);
    				break;
    			}
    			if (v != null) vehicles.add(v);
    		}
    	}
    	catch (Exception e ) {
    		System.out.println("Error loading vehicles: " + e.getMessage());
    		
    	}
    	try (BufferedReader br = new BufferedReader(new FileReader("customer.txt"))){
    		String line;
    		while ((line = br.readLine()) != null) {
    			String[] parts = line.split(",");
    			if (parts.length < 2) continue;
    			int id = Integer.parseInt(parts[0]);
    			String name = parts[1];
    			customers.add(new Customer(id, name));
    		}
    	}
    	catch (Exception e) {
    		System.out.println("Error loading customers: " + e.getMessage());
    	}
    	
    	try (BufferedReader br = new BufferedReader(new FileReader("rental_records.txt"))){
    		String line;
    		while ((line = br.readLine()) != null) {
    			String[] parts = line.split(",");
    			if (parts.length < 5) continue;
    			String recordType = parts[0];
    			String plate = parts[1];
    			String customerName = parts[2];
    			LocalDate date = LocalDate.parse(parts[3]);
    			double amount = Double.parseDouble(parts[4]);
    			
    			Vehicle v = findVehicleByPlate(plate);
    			Customer c = customers.stream().filter(cust -> cust.getCustomerName().equals(customerName)).findFirst().orElse(null);
    			if (v != null && c != null) {
    				rentalHistory.addRecord(new RentalRecord(v, c, date, amount, recordType));
    				if (recordType.equals("RENT")) {
    					v.setStatus(Vehicle.VehicleStatus.Rented);;
    					
    				}
    				else if (recordType.equals("RETURN")) {
    					v.setStatus(Vehicle.VehicleStatus.Available);
    				}
    			}
    					
    					
    		}
    	}
    	catch (Exception e) {
    		System.out.println("Error loading rental records: " + e.getMessage());
    		
    	}
    }
    
    	
    

    public boolean addVehicle(Vehicle vehicle) {
    	if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
    		System.out.println("Error, the license plate" + vehicle.getLicensePlate() + "already exists");
    		return false;
    	}
    	
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        System.out.println("Vehicle added successfully");
        return true;
    }

    public boolean addCustomer(Customer customer) {
    	if (findCustomerById(customer.getCustomerId())!= null) {
    		System.out.println("Error, the ID" + customer.getCustomerId() + "already exists");
    		return false;
    	}
        customers.add(customer);
        saveCustomer(customer);
        System.out.println("Customer added successfully");
        return true;
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            
            RentalRecord r = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(r);
            saveRecord(r);
            
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
           
            RentalRecord r = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(r);
            saveRecord(r);
           
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not rented.");
        }
    }    

    public void displayVehicles(Vehicle.VehicleStatus status) {
        // Display appropriate title based on status
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        
        // Header with proper column widths
        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
    	  
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n", 
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            // Header with proper column widths
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");
            
            for (RentalRecord record : rentalHistory.getRentalHistory()) {                
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(), 
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }
}
    	}
    	return instance;
    }
    private void saveVehicle(Vehicle v) {
    	try (FileWriter writer = new FileWriter("vehicle.txt", true)){
    		String type;
    		String extra = "";
    		
    		if (v instanceof Car) {
    			type = "Car";
    			Car c = (Car) v;
    			extra = String.valueOf(c.getNumSeats());
    		}
    		else if (v instanceof Minibus) {
    			type = "Minibus";
    			Minibus m = (Minibus) v;
    			extra = String.valueOf(m.getInfo().contains("Yes"));
    		}
    		else if (v instanceof PickupTruck) {
    			type = "PickupTruck";
    			PickupTruck p = (PickupTruck) v;
    			extra = p.getCargoSize() + "|" + p.hasTrailer();
    		}
    		else {
    			type = "unknown";
    		}
    		writer.write(v.getLicensePlate() + "," + v.getMake() + "," + v.getModel() + "," + v.getYear() + "," + type + "," + extra + "\n");
    		
    	}
    	catch (IOException e) {
    		System.out.println("Error saving vehicle: " + e.getMessage());
    	}
    }
    private void saveCustomer(Customer c) {
    	try (FileWriter writer = new FileWriter("customers.txt", true)){
    		writer.write(c.getCustomerId() + "," + c.getCustomerName() + "\n");
    		
    	}
    	catch (IOException e) {
    		System.out.println("Error saving customer: " + e.getMessage());
    	}
    }
    private void saveRecord(RentalRecord r) {
    	try (FileWriter writer = new FileWriter("rental_records.txt", true)){
    		writer.write(r.getRecordType() + "," + r.getVehicle().getLicensePlate() + "," + r.getCustomer().getCustomerName() + "," + r.getRecordDate().toString() + "," + r.getTotalAmount() + "\n");
    		
    	}
    	catch (IOException e) {
    		System.out.println("Error saving rental records: " + e.getMessage());
    	}
    }
    private void loadData() {
    	try (BufferedReader br = new BufferedReader(new FileReader("vehicle.txt"))){
    		String line;
    		while ((line = br.readLine()) != null) {
    			String[] parts = line.split(",");
    			if (parts.length < 6) continue;
    			String plate = parts[0];
    			String make = parts[1];
    			String model = parts[2];
    			int year = Integer.parseInt(parts[3]);
    			String type = parts[4];
    			String extra = parts[5];
    			
    			Vehicle v = null;
    			switch (type) {
    			case "Car": 
    				v = new Car(plate, make, model, year, Integer.parseInt(extra));
    			break;
    			case "Minibus": 
    				v = new Minibus(plate, make, model, year, Boolean.parseBoolean(extra));
    			break;
    			case "PickupTruck": 
    				String[] pickupExtra = extra.split("\\|");
    				double cargoSize = Double.parseDouble(pickupExtra[0]);
    				boolean hasTrailer = Boolean.parseBoolean(pickupExtra[1]);
    				v = new PickupTruck(plate, make, model, year, cargoSize, hasTrailer);
    				break;
    			}
    			if (v != null) vehicles.add(v);
    		}
    	}
    	catch (Exception e ) {
    		System.out.println("Error loading vehicles: " + e.getMessage());
    		
    	}
    	try (BufferedReader br = new BufferedReader(new FileReader("customer.txt"))){
    		String line;
    		while ((line = br.readLine()) != null) {
    			String[] parts = line.split(",");
    			if (parts.length < 2) continue;
    			int id = Integer.parseInt(parts[0]);
    			String name = parts[1];
    			customers.add(new Customer(id, name));
    		}
    	}
    	catch (Exception e) {
    		System.out.println("Error loading customers: " + e.getMessage());
    	}
    	
    	try (BufferedReader br = new BufferedReader(new FileReader("rental_records.txt"))){
    		String line;
    		while ((line = br.readLine()) != null) {
    			String[] parts = line.split(",");
    			if (parts.length < 5) continue;
    			String recordType = parts[0];
    			String plate = parts[1];
    			String customerName = parts[2];
    			LocalDate date = LocalDate.parse(parts[3]);
    			double amount = Double.parseDouble(parts[4]);
    			
    			Vehicle v = findVehicleByPlate(plate);
    			Customer c = customers.stream().filter(cust -> cust.getCustomerName().equals(customerName)).findFirst().orElse(null);
    			if (v != null && c != null) {
    				rentalHistory.addRecord(new RentalRecord(v, c, date, amount, recordType));
    				if (recordType.equals("RENT")) {
    					v.setStatus(Vehicle.VehicleStatus.Rented);;
    					
    				}
    				else if (recordType.equals("RETURN")) {
    					v.setStatus(Vehicle.VehicleStatus.Available);
    				}
    			}
    					
    					
    		}
    	}
    	catch (Exception e) {
    		System.out.println("Error loading rental records: " + e.getMessage());
    		
    	}
    }
    
    	
    

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle(vehicle);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomer(customer);
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            
            RentalRecord r = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(r);
            saveRecord(r);
            
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
           
            RentalRecord r = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(r);
            saveRecord(r);
           
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not rented.");
        }
    }    

    public void displayVehicles(Vehicle.VehicleStatus status) {
        // Display appropriate title based on status
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        
        // Header with proper column widths
        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
    	  
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n", 
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            // Header with proper column widths
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");
            
            for (RentalRecord record : rentalHistory.getRentalHistory()) {                
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(), 
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }
}
