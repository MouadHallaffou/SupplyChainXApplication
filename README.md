## Strecture of the Project

```
com.supplychainx
│
├── user/
│   ├── controller/
│   │   ├── RoleController.java
│   │   └── UserController.java
│   ├── service/
│   │   ├── RoleService.java
│   │   └── UserService.java
│   ├── repository/
│   │   ├── RoleRepository.java
│   │   └── UserRepository.java
│   ├── model/
│   │   ├── Role.java
│   │   └── User.java
│   ├── dto/
│   │   ├── RoleDTO.java
│   │   └── UserDTO.java
│   └── mapper/
│       ├── RoleMapper.java
│       └── UserMapper.java
│
├── exception/
│   ├── ResourceNotFoundException.java
│   └── DuplicateEmailException.java
│
├── handler/
│   └── GlobalExceptionHandler.java
│
└── util/
    ├── PasswordUtil.java
    ├── DateUtil.java
    └── ValidationUtil.java
```