package com.ideas2it.luxitrip.controller;

import java.io.IOException;

import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; 
import javax.servlet.http.HttpSession; 

import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;    
import org.springframework.stereotype.Controller;    
import org.springframework.web.bind.annotation.ModelAttribute;   
import org.springframework.web.bind.annotation.RequestMapping; 

import com.ideas2it.luxitrip.service.UserService;
import com.ideas2it.luxitrip.model.User;
import com.ideas2it.luxitrip.model.Bus;
import com.ideas2it.luxitrip.exception.CustomException;

@Controller
public class UserController {
	
    @Autowired
    private UserService userService;
	
    /**
     * Method used to validate the user by using the userName and password in the database
     * @param request{@link HttpServletRequest}
     * @param response{@link HttpServletResponse}
     * @return the confirmation to the User 
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/login")
    public ModelAndView validateUser(HttpServletRequest request, 
        HttpServletResponse response) throws ServletException, IOException {
        ModelAndView model = new ModelAndView();
        try {
            User user = userService.retrieveUserByName(request.getParameter("userName"));
            String role = userService.redirectPage(user, request.getParameter("password")); 
            if(role.equals("User")) {
                HttpSession session = request.getSession();
                session.setAttribute("userId", user.getId());
                model.addObject("user", user);
                model.setViewName("userIndex");
                return model;
            } else {
                HttpSession session = request.getSession();
                session.setAttribute("userId", user.getId());
                List<Bus> buses = userService.retrieveBuses();
                model.addObject("buses", buses);
                model.addObject("user", user);
                model.setViewName("adminpage");
                return model;
            }
        } catch(CustomException ex) {
            return new ModelAndView("Message", "message", ex.getMessage());
        }
    }
    
    /**
     * Method used to register the user and its detail in the database
     * @param request{@link HttpServletRequest}
     * @param response{@link HttpServletResponse}
     * @return the confirmation to the User 
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/registerUser")
    public ModelAndView registerUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        User user = new User();
        ModelAndView model = new ModelAndView();
        try {
            user.setName(request.getParameter("name"));
            user.setNumber(request.getParameter("number"));
            user.setPassword(request.getParameter("password"));
            user.setEmailId(request.getParameter("emailId"));
            user.setRole(request.getParameter("role"));
            userService.createUser(user);
            if(user.getRole().equals("Driver") || user.getRole().equals("Admin")) {
                List<Bus> buses = userService.retrieveBuses();
                model.addObject("buses", buses);
                model.addObject("user", user);
                model.setViewName("adminpage");
                return model;
            } else {
                return new ModelAndView("login", "message", "User register Successfully");
            }
        } catch (CustomException ex) {
            return new ModelAndView("login",  "message", ex.getMessage());
        }
    }
	
    /**
     * Method used to register the driver and its details in the object
     * @return the user object to register the driver details
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/createDriver")
    public ModelAndView registerDriver() throws ServletException, IOException {
        return new ModelAndView("registerDriver", "user", new User());
    }
	
    /**
     *  Method used to display the list of users in the database 
     * @return the list of users
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/displayUsers")
    public ModelAndView displayUsers() throws ServletException, IOException {
        try {
            return new ModelAndView("displayUsers", "users", userService.retrieveUsers());
        } catch (CustomException ex) {
            return new ModelAndView("Message", "message", ex.getMessage());
        }
    }
	
    /**
     * Method used to get the User from the database by using the userId
     * @param request
     * @param response
     * @return the userObject to the requested page
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/displayUser")
    public ModelAndView getUserById(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            return new ModelAndView("updateTheatreForm", "user", 
                    userService.retrieveUserById((Integer.parseInt(request.getParameter("userId")))));
        } catch(CustomException ex) {
            return new ModelAndView("Message", "message", ex.getMessage());
        }
    }

    /**
     * Method used to update the user detail from the existing user detail
     * @param user - it get the updated user detail
     * @return the confirmation to the user
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/updateUser")
    public ModelAndView updateUser(HttpServletRequest request, 
            HttpServletResponse response, @ModelAttribute("user")User user) throws ServletException, IOException {
        ModelAndView model = new ModelAndView();
        try {
            System.out.println(user);
            if(user.getRole().equals("Admin")) {
                List<Bus> buses = userService.retrieveBuses();
                model.addObject("buses", buses);
                model.addObject("user", user);
                model.setViewName("adminpage");
                return model;
            } else if(user.getRole().equals("User")){
                model.addObject("user", user);
                model.setViewName("userIndex");
                return model;
            } else {
                return new ModelAndView("Message", "message", "Driver Added Successfully");
            }
        } catch(CustomException ex) {
            return new ModelAndView("Message", "message", ex.getMessage());
        }
	}
	
    /**
     * Method used to delete the User from the database by using the UserId 
     * given from the User
     * @return the confirmation to the user
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/removeUser")
    public ModelAndView deleteUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            userService.deleteUser
                    ((Integer.parseInt(request.getParameter("userId"))));
            return new ModelAndView("Message", "message", "User deleted Successfully");
        } catch(CustomException ex) {
            return new ModelAndView("Message", "message", ex.getMessage());
        }
    }
}
