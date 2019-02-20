package com.example.demo.model;

import java.util.Set;

public class User  {

	private static final long serialVersionUID = 1L;
	
    private int id;
    
    private String email;
    
    private String password;
    
    private String name;
    
    private String lastName;
    
    private int active;
    
    private Set<Role> roles;
    
    private Boolean enabled;
    
    private Boolean accountNonLocked;
    
    private Boolean accountNonExpired;
    
    private Boolean credentialsNonExpired;
    
    private String username;
    
    public User() {
		super();
	}
    
 	public User(String email, String password, String name, String lastName, int active, Set<Role> roles) {
		super();
	
		this.email = email;
		this.password = password;
		this.name = name;
		this.lastName = lastName;
		this.active = active;
		this.roles = roles;
	}
 	
 	@Override
    public String toString() {
        String result = String.format(
                "User [id=%d, name='%s']%n",id, name);
        if (roles != null) {
            for(Role role : roles) {
                result += String.format(
                        "Role[id=%d, name='%s']%n",
                        role.getRoleId(), role.getRole());
            }
        }

        return result;
    }
 	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return this.password;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public Boolean getAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(Boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public Boolean getCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}
}