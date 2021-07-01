package bookingApplication.jdo

data class Service(var serviceName : String, var serviceDuration : Long, var serviceCost : Long, var serviceProvider : ArrayList<Staff?> = arrayListOf())
