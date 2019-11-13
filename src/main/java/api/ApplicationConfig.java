package api;

import api.service.CompanyServices;
import api.service.DepartmentServices;
import api.service.EmployeeServices;
import api.service.TimecardServices;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("resources")
public class ApplicationConfig extends Application {
	@Override
	public Set<Class<?>> getClasses() {
		return getRestResourceClasses();
	}

	private Set<Class<?>> getRestResourceClasses() {
		Set<Class<?>> resources = new HashSet<>();
		resources.add(CompanyServices.class);
		resources.add(EmployeeServices.class);
		resources.add(TimecardServices.class);
		resources.add(DepartmentServices.class);

		return resources;
	}
}
