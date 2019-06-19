package com.seamfix.login.models;

import javax.enterprise.context.RequestScoped;

import lombok.Setter;

import lombok.Getter;

@Getter
@Setter
@RequestScoped
public class QueryData {
 private String email;
 private String password;
}