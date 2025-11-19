import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; 
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); 
System.out.println(encoder.encode(\" "Admin@123\)); 
