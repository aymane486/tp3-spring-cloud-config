package com.mproduits.dao;

import com.mproduits.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository : bean Spring dont le role est de communiquer avec une source de donnees (ici H2).
@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {
}
