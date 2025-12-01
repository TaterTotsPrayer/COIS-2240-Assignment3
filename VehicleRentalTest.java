/*
 * Name: Anthony Dinh
 * Description: Making test cases to make sure it validates license plates properly,
 * makes sure that the rent and return functions work correctly, and makes sure that RentalSystem
 * is Singleton.
 */

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;


class VehicleRentalTest {
	
	private RentalSystem rentalSystem;
	private Vehicle vehicle;
	private Customer customer;
	
	
	@BeforeEach
	void setUp() 
	{
		rentalSystem = RentalSystem.getInstance();
		vehicle = new Car("ABC123", "Toyota", "Camry", 2020, 4);
		customer = new Customer(1, "John Doe");
		
		rentalSystem.addVehicle(vehicle);
	}
	
	@Test
	void testLicensePlate() {
		
		Vehicle v1 = new Car("AAA100", "Toyota", "Corolla", 2020, 4);
		assertEquals("AAA100", v1.getLicensePlate());
		
		Vehicle v2 = new Car("ABC567", "Honda", "Civic", 2020, 4);
		assertEquals("ABC567", v2.getLicensePlate());
		
		Vehicle v3 = new Car("ZZZ999", "Ford", "Focus", 2020, 4);
		assertEquals("ZZZ999", v3.getLicensePlate());
		
		
		assertThrows(IllegalArgumentException.class, () -> {
			new Car("", "Toyota", "Camry", 2020, 4);
        });
		
		assertThrows(IllegalArgumentException.class, () -> {
			new Car(null, "Toyota", "Camry", 2020, 4);
        });
		
		assertThrows(IllegalArgumentException.class, () -> {
			new Car("AAA1000", "Toyota", "Camry", 2020, 4);
        });
		
		assertThrows(IllegalArgumentException.class, () -> {
			new Car("ZZZ99", "Toyota", "Camry", 2020, 4);
        });
	}
	
	@Test
	void testRentAndReturnVehicle() {
		
		assertEquals(Vehicle.VehicleStatus.Available, vehicle.getStatus());
		
		boolean rentSuccess = rentalSystem.rentVehicle(vehicle, customer, LocalDate.now(), 100.0);
		assertTrue(rentSuccess, "Rent succeeds");
		assertEquals(Vehicle.VehicleStatus.Rented, vehicle.getStatus());
		
		boolean rentAgain = rentalSystem.rentVehicle(vehicle, customer, LocalDate.now(), 100.0);
		assertFalse(rentAgain, "Renting same vehicle fails");
		
		boolean returnSuccess = rentalSystem.returnVehicle(vehicle, customer, LocalDate.now(), 0.0);
		assertTrue(returnSuccess, "Return succeeds");
		assertEquals(Vehicle.VehicleStatus.Available, vehicle.getStatus());
		
		boolean returnAgain = rentalSystem.returnVehicle(vehicle, customer, LocalDate.now(), 0.0);
		assertFalse(returnAgain, "Returning same vehicle fails");
	}
	
	@Test
	void testSingletonRentalSystem() throws Exception{
		Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
		int modifiers = constructor.getModifiers();
		assertTrue(Modifier.isPrivate(modifiers), "Constructor is private");
		
		RentalSystem instance = RentalSystem.getInstance();
		assertNotNull(instance, "Singleton instance not null");
	}
}
