package com.example.kotlin

import java.lang.NullPointerException
import java.util.*
import bookingApplication.jdo.*
import java.text.*;
fun GetFromUserString(userInput : Scanner, InputFieldName : String) : String{
    println("Enter The $InputFieldName : ")
    return userInput.nextLine();
}
fun GetFromUserLong(userInput : Scanner, InputFieldName : String) : Long{
    println("Enter The $InputFieldName : ")
    return userInput.nextLong();
}
fun StaffDetails(service : Service?) : Int{
    var count : Int = 0
    for(staff_name in service!!.serviceProvider){
        if(staff_name != null){
            count++
            println( "    ${staff_name!!.firstName + staff_name!!.lastName } : ${staff_name!!.id} ")
        }
    }
    return count
}
fun main() {
    var userInput = Scanner(System.`in`)
    var serviceList = hashMapOf<String, Service>()
    var staffList = hashMapOf<String, Staff>()
    var customerList = hashMapOf<String, Customer>()
    var bookingList = hashMapOf<String, BookingDetails>()
    var customerCount : Long = 10001
    var staffCount : Long = 10001
    var serviceName : String = ""
    var serviceDuration : Long = 0
    var serviceCost : Long = 0
    var firstName : String = ""
    var lastName : String = ""
    var start_time : Date
    var end_time : Date
    var customerMobileNumber : Long = 0
    var userChoice = 0
    fun serviceAvailable() : Int{
        var count : Int = 0
        for (service in serviceList.keys)
        {
            count++
            println("    $service")
        }
        return count
    }
    var pattern = SimpleDateFormat("hh:mm a")
    while(true){
        println("1.Create Service.\n2.Create Staff.\n3.Create Customer.\n4.Book Appointment.\n5.Forget Id")
        try {
            userChoice = userInput.nextInt()
            if (userChoice == 1) {
                try {
                    userInput.nextLine()
                    serviceName = GetFromUserString(userInput, "Service Name")
                    serviceDuration = GetFromUserLong(userInput, "Service Duration in minutes")
                    serviceCost = GetFromUserLong(userInput, "Cost for Service")
                    var service = Service(serviceName, serviceDuration, serviceCost)
                    serviceList.put(serviceName, service)
                } catch (e: Exception) {
                    userInput.nextLine();
                    println("Invalid Input")
                }
            } else if (userChoice == 2) {
                userInput.nextLine()
                firstName = GetFromUserString(userInput, "Staff First Name")
                lastName = GetFromUserString(userInput, "Staff Last Name")
                var startTime = GetFromUserString(userInput, "Starting Time (eg : 10:00 AM)")
                var endTime = GetFromUserString(userInput, "End Time (eg : 10:00 PM)")
                try {
                    start_time = pattern.parse(startTime)
                    end_time = pattern.parse(endTime)
                    if( end_time > start_time && end_time < pattern.parse("11:59 PM")) {
                        var workingHour = WorkingHour(start_time, end_time)
                        var staff = Staff(
                            firstName,
                            lastName,
                            workingHour,
                            firstName + staffCount
                        )
                        println("Service Available, if your service not in list you can create by Create Service...")
                        var flag: Int = serviceAvailable()
                        if (flag > 0) {
                            try {
                                serviceName = GetFromUserString(userInput, "Service Name")
                                staffList.put(firstName + staffCount, staff)
                                var service = serviceList.get(serviceName)
                                service!!.serviceProvider.add(staff)
                                println("Here's your Staff ID : ${firstName + staffCount}")
                                staffCount++
                            } catch (e: NullPointerException) {
                                println("service your entered is not available")
                            }
                        } else {
                            println("There is No Available Service")
                        }
                    }
                    else{
                        println("End Time should be with in 12-AM")
                    }
                }catch (e:java.lang.Exception){
                    println("Enter the Start Time and End Time in the format of 10:00  AM/PM. AM/PM must be in capital")
                }
            } else if (userChoice == 3) {
                userInput.nextLine()
                firstName = GetFromUserString(userInput, "Customer First Name")
                lastName = GetFromUserString(userInput, "Customer Last Name")
                customerMobileNumber = GetFromUserLong(userInput, "Customer Mobile Number")
                var customer = Customer( firstName + customerCount,firstName,lastName,customerMobileNumber)
                println("Here's your Customer ID : ${firstName + customerCount}")
                customerList.put(firstName + customerCount, customer)
                customerCount++
                println(customerList)
            } else if (userChoice == 4) {
                userInput.nextLine()
                println("Service Available, if your service not in list you can create by Create Service...")
                var flag: Int = serviceAvailable()
                if (flag > 0) {
                    try {
                        serviceName = GetFromUserString(userInput, "Select Service available")
                        var service = serviceList.get(serviceName)
                        var flag: Int = StaffDetails(service)
                        if (flag > 0) {
                            var staff_id = GetFromUserString(userInput, "Select Staff ID")
                            var staff = staffList.get(staff_id)
                            var currenttime = Calendar.getInstance().time
                            var currentTime = pattern.parse(pattern.format(currenttime).toString()).time
                            var start_time = staff!!.workingHours.startTime.time
                            var end_time = staff!!.workingHours.endTime.time
                            var duration: Long = service!!.serviceDuration
                            if(duration > ((end_time - start_time) / (60 * 1000) ) ){
                                throw Exception("Duration of service is higher than staff working hour")
                            }
                            var flag = 0
                            println("Available sloat is :")
                            if (currentTime <= end_time) {
                                var duration_between = currentTime + (duration * 60000)
                                if( currentTime < start_time){
                                    duration_between = start_time
                                }
                                do  {
                                    var startduration = duration_between
                                    duration_between = duration_between + (duration * 60000)
                                    if(duration_between > end_time )
                                    {
                                        break
                                    }
                                    println("  ${pattern.format(startduration) }  - ${pattern.format(duration_between)}")
                                    flag++
                                }while(duration_between < end_time);
                                if(flag != 0) {
                                    var timeSlot =
                                        GetFromUserString(userInput, "time slot from above list")
                                    var Customer_id = GetFromUserString(userInput, "Customer ID")
                                    var customer = customerList.get(Customer_id)
                                    try {
                                        var bookingDetails = BookingDetails(
                                            timeSlot,
                                            serviceName,
                                            staff_id,
                                            Customer_id
                                        )
                                        bookingList.put(Customer_id,bookingDetails)
                                        println(
                                            "Booking Status...\n\tCustomer Id : ${customer!!.id},\n" +
                                                    "\tCustomer Name : ${
                                                        customer!!.firstName + " " + customer!!.lastName
                                                    },\n" +
                                                    "\tCustomer Mobile Number : ${
                                                        customer.mobileNumber
                                                    },\n" +
                                                    "\tCustomer Booking Time : ${
                                                        bookingDetails.bookingTime
                                                    },\n" +
                                                    "\tCustomer Booking Service : ${
                                                        bookingDetails.bookingService
                                                    },\n" +
                                                    "\tStaff for the Service : ${
                                                        bookingDetails.staff
                                                    }\n" +
                                                    "\tCost for the Service : ${
                                                        service.serviceCost
                                                    }"
                                        )
                                    } catch (e: NullPointerException) {
                                        println("Customer Id your entered is not available You can Know You Customer id by Forget Id")
                                    }
                                }
                            }
                        } else {
                            println("There is No Available Staff for this Service")
                        }
                    } catch (e: NullPointerException) {
                        println("service your entered is not available")
                    }
                }
                else{
                    println("There is No Available Service")
                }
            }
            else if(userChoice == 5){
                customerMobileNumber = GetFromUserLong(userInput,"Mobile Number")
                var flag = 0
                for(customer in customerList.values)
                {
                    var mobileNumber = customer.mobileNumber
                    if(mobileNumber.equals(customerMobileNumber)){
                        println("Here's Your Customer Id : ${ customer.id}")
                        flag = 1
                        break
                    }
                }
                if(flag == 0){
                    println("There is No customer linked with this Mobile Number $customerMobileNumber")
                }
            }
        }
        catch (E : InputMismatchException){
            userInput.nextLine();
            println("Invalid Input")
        }
        catch (E : Exception){
            println("Exception Occurs $E")
        }
    }
}

