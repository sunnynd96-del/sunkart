package com.sunKart.controller;

import com.sunKart.model.Role;
import com.sunKart.model.User;
import com.sunKart.repository.RoleRepository;
import com.sunKart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/setup")
    @ResponseBody
    public String setup() {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial; padding: 20px;'>");
        html.append("<h1>🛒 SunKart Setup Page</h1>");
        
        try {
            var adminOpt = userService.findByUsername("admin");
            
            if (adminOpt.isPresent()) {
                User admin = adminOpt.get();
                boolean matches = passwordEncoder.matches("admin123", admin.getPassword());
                
                html.append("<div style='background: #d4edda; padding: 15px; border-radius: 5px; margin: 20px 0;'>");
                html.append("<h2>✅ Admin User Exists</h2>");
                html.append("<p><strong>Username:</strong> admin</p>");
                html.append("<p><strong>Password Test:</strong> ");
                html.append(matches ? "✅ MATCHES (admin123)" : "❌ DOES NOT MATCH");
                html.append("</p>");
                html.append("<p><strong>Roles:</strong> " + admin.getRoles().size() + "</p>");
                html.append("</div>");
                
                html.append("<br><a href='/check-roles' style='padding: 10px 20px; background: #17a2b8; color: white; text-decoration: none; border-radius: 5px; margin-right: 10px;'>🔍 Check Roles</a>");
                
                if (matches) {
                    html.append("<a href='/login' style='padding: 10px 20px; background: #28a745; color: white; text-decoration: none; border-radius: 5px;'>➡️ Go to Login</a>");
                }
            } else {
                html.append("<div style='background: #fff3cd; padding: 15px; border-radius: 5px; margin: 20px 0;'>");
                html.append("<h2>⚠️ Admin User Not Found</h2>");
                html.append("</div>");
                html.append("<a href='/create-admin' style='padding: 15px 30px; background: #007bff; color: white; text-decoration: none; border-radius: 5px;'>➕ Create Admin Now</a>");
            }
            
        } catch (Exception e) {
            html.append("<div style='background: #f8d7da; padding: 15px; border-radius: 5px;'>");
            html.append("<h2>❌ Error</h2>");
            html.append("<p>" + e.getMessage() + "</p>");
            html.append("</div>");
        }
        
        html.append("</body></html>");
        return html.toString();
    }
    
    @GetMapping("/check-roles")
    @ResponseBody
    public String checkRoles() {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial; padding: 20px;'>");
        html.append("<h1>🔍 Current User Role Check</h1>");
        
        try {
            var adminOpt = userService.findByUsername("admin");
            
            if (adminOpt.isPresent()) {
                User admin = adminOpt.get();
                
                html.append("<div style='background: #e7f3ff; padding: 15px; border-radius: 5px;'>");
                html.append("<h2>Admin User Details:</h2>");
                html.append("<p><strong>Username:</strong> ").append(admin.getUsername()).append("</p>");
                html.append("<p><strong>Email:</strong> ").append(admin.getEmail()).append("</p>");
                html.append("<p><strong>Enabled:</strong> ").append(admin.isEnabled()).append("</p>");
                html.append("<p><strong>Number of Roles:</strong> ").append(admin.getRoles().size()).append("</p>");
                
                html.append("<h3>Roles Details:</h3>");
                html.append("<ul>");
                
                if (admin.getRoles().isEmpty()) {
                    html.append("<li style='color: red; font-size: 18px;'><strong>❌ NO ROLES ASSIGNED!</strong></li>");
                } else {
                    for (Role role : admin.getRoles()) {
                        html.append("<li style='font-size: 16px;'>");
                        html.append("<strong>Role ID:</strong> ").append(role.getId());
                        html.append(" | <strong>Role Name:</strong> <span style='color: blue;'>").append(role.getName()).append("</span>");
                        html.append("</li>");
                    }
                }
                html.append("</ul>");
                html.append("</div>");
                
                // Fix button if no roles or wrong role
                boolean hasAdminRole = admin.getRoles().stream()
                    .anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
                
                if (admin.getRoles().isEmpty() || !hasAdminRole) {
                    html.append("<br><a href='/assign-admin-role' style='padding: 15px 30px; background: #dc3545; color: white; text-decoration: none; border-radius: 5px; display: inline-block; font-size: 18px;'>🔧 Assign ADMIN Role Now</a>");
                } else {
                    html.append("<br><div style='background: #d4edda; padding: 15px; border-radius: 5px; margin-top: 20px;'>");
                    html.append("<h3>✅ Admin role is correct!</h3>");
                    html.append("<p><a href='/logout'>Logout</a> and <a href='/login'>Login again</a>, then try <a href='/admin'>/admin</a></p>");
                    html.append("</div>");
                }
                
            } else {
                html.append("<p style='color: red;'>Admin user not found!</p>");
            }
            
        } catch (Exception e) {
            html.append("<p style='color: red;'>Error: ").append(e.getMessage()).append("</p>");
        }
        
        html.append("</body></html>");
        return html.toString();
    }

    @GetMapping("/assign-admin-role")
    @ResponseBody
    public String assignAdminRole() {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial; padding: 20px;'>");
        html.append("<h1>🔧 Assigning ADMIN Role...</h1>");
        
        try {
            var adminOpt = userService.findByUsername("admin");
            
            if (adminOpt.isEmpty()) {
                html.append("<p style='color: red; font-size: 18px;'>❌ Admin user not found!</p>");
                html.append("<a href='/create-admin'>Create Admin</a>");
                return html.append("</body></html>").toString();
            }
            
            User admin = adminOpt.get();
            
            // Get or create ROLE_ADMIN
            var adminRoleOpt = roleRepository.findByName("ROLE_ADMIN");
            Role adminRole;
            
            if (adminRoleOpt.isEmpty()) {
                // Create ROLE_ADMIN
                adminRole = new Role("ROLE_ADMIN");
                adminRole = roleRepository.save(adminRole);
                html.append("<p>✅ Created new ROLE_ADMIN</p>");
            } else {
                adminRole = adminRoleOpt.get();
                html.append("<p>✅ Found existing ROLE_ADMIN (ID: ").append(adminRole.getId()).append(")</p>");
            }
            
            // Assign role to admin user
            admin.getRoles().clear();
            admin.getRoles().add(adminRole);
            userService.saveUser(admin);
            
            html.append("<div style='background: #d4edda; padding: 20px; border-radius: 5px; margin: 20px 0;'>");
            html.append("<h2>✅ SUCCESS!</h2>");
            html.append("<p style='font-size: 16px;'>ROLE_ADMIN has been assigned to admin user</p>");
            html.append("<p><strong>Role Name:</strong> ").append(adminRole.getName()).append("</p>");
            html.append("</div>");
            
            html.append("<div style='background: #fff3cd; padding: 15px; border-radius: 5px;'>");
            html.append("<h3>📋 Next Steps:</h3>");
            html.append("<ol style='font-size: 16px;'>");
            html.append("<li><a href='/logout' style='color: #dc3545; font-weight: bold;'>Logout</a> (Important!)</li>");
            html.append("<li><a href='/login' style='color: #007bff; font-weight: bold;'>Login again</a> with admin/admin123</li>");
            html.append("<li><a href='/admin' style='color: #28a745; font-weight: bold;'>Try Admin Dashboard</a></li>");
            html.append("</ol>");
            html.append("</div>");
            
        } catch (Exception e) {
            html.append("<div style='background: #f8d7da; padding: 15px;'>");
            html.append("<p style='color: red; font-size: 18px;'>❌ Error: ").append(e.getMessage()).append("</p>");
            html.append("</div>");
        }
        
        html.append("</body></html>");
        return html.toString();
    }
    
    @GetMapping("/create-admin")
    @ResponseBody
    public String createAdmin() {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial; padding: 20px;'>");
        html.append("<h1>Creating Admin User...</h1>");
        
        try {
            if (userService.findByUsername("admin").isPresent()) {
                html.append("<div style='background: #fff3cd; padding: 15px;'>");
                html.append("<h2>⚠️ Admin Already Exists!</h2>");
                html.append("<p><a href='/setup'>Go back to setup</a></p>");
                html.append("</div>");
            } else {
                User admin = userService.registerAdmin("admin", "admin@sunkart.com", "admin123");
                
                if (admin != null) {
                    boolean matches = passwordEncoder.matches("admin123", admin.getPassword());
                    
                    html.append("<div style='background: #d4edda; padding: 15px; border-radius: 5px;'>");
                    html.append("<h2>✅ SUCCESS! Admin Created!</h2>");
                    html.append("<p><strong>Username:</strong> admin</p>");
                    html.append("<p><strong>Password:</strong> admin123</p>");
                    html.append("<p><strong>Password Test:</strong> " + (matches ? "✅ MATCHES" : "❌ DOES NOT MATCH") + "</p>");
                    html.append("</div>");
                    html.append("<br><a href='/check-roles' style='padding: 10px 20px; background: #17a2b8; color: white; text-decoration: none; border-radius: 5px;'>Check Roles</a>");
                    html.append(" <a href='/login' style='padding: 10px 20px; background: #007bff; color: white; text-decoration: none; border-radius: 5px;'>Go to Login</a>");
                } else {
                    html.append("<div style='background: #f8d7da; padding: 15px;'>");
                    html.append("<h2>❌ Failed to Create Admin</h2>");
                    html.append("</div>");
                }
            }
        } catch (Exception e) {
            html.append("<div style='background: #f8d7da; padding: 15px;'>");
            html.append("<h2>❌ Error: " + e.getMessage() + "</h2>");
            html.append("</div>");
        }
        
        html.append("</body></html>");
        return html.toString();
    }
}