package com.example.demo.model;

import java.util.Set;

//@Entity
//@Table(name = "role")
public class Role {

  //  @Id
  //  @GeneratedValue(strategy = GenerationType.AUTO)
  //  @Column(name = "role_id")
    private int roleId;

    //@Column(name = "role")
    private String role;
    
    //@Column(name = "users")
    //@ManyToMany(mappedBy = "roles")
    //@JsonIgnoreProperties("roles")
    private Set<User> users;
    
    public Role() {
    }
    
    public Role(int roleId, String role, Set<User> users) {
		super();
		this.roleId = roleId;
		this.role = role;
		this.users = users;
	}

	public Role(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "Role [role=" + role + "]";
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}