package com.mproduits.web.controller;

import com.mproduits.configurations.ApplicationPropertiesConfiguration;
import com.mproduits.dao.ProductDao;
import com.mproduits.model.Product;
import com.mproduits.web.exceptions.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductController implements HealthIndicator {

	@Autowired
	ProductDao productDao;

	@Autowired
	ApplicationPropertiesConfiguration appProperties;

	// Affiche la liste des produits, limitee par mes-configs.limitDeProduits
	@GetMapping(value = "/Produits")
	public List<Product> listeDesProduits() {
		System.out.println(" ********* ProductController listeDesProduits() ");
		List<Product> products = productDao.findAll();
		if (products.isEmpty())
			throw new ProductNotFoundException("Aucun produit n'est disponible à la vente");

		// Math.min evite une IndexOutOfBoundsException si la limite > nombre de produits
		int limite = Math.min(appProperties.getLimitDeProduits(), products.size());
		return products.subList(0, limite);
	}

	// Recuperer un produit par son id
	@GetMapping(value = "/Produits/{id}")
	public Optional<Product> recupererUnProduit(@PathVariable int id) {
		System.out.println(" ********* ProductController recupererUnProduit(@PathVariable int id) ");
		Optional<Product> product = productDao.findById(id);
		if (!product.isPresent())
			throw new ProductNotFoundException("Le produit correspondant à l'id " + id + " n'existe pas");
		return product;
	}

	// Ajouter un produit (POST)
	@PostMapping(value = "/Produits")
	@ResponseStatus(HttpStatus.CREATED)
	public Product ajouterProduit(@RequestBody Product product) {
		product.setId(0);
		return productDao.save(product);
	}

	// Modifier un produit (PUT)
	@PutMapping(value = "/Produits/{id}")
	public Product modifierProduit(@PathVariable int id, @RequestBody Product product) {
		if (!productDao.existsById(id))
			throw new ProductNotFoundException("Le produit correspondant à l'id " + id + " n'existe pas");
		product.setId(id);
		return productDao.save(product);
	}

	// Supprimer un produit (DELETE)
	@DeleteMapping(value = "/Produits/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void supprimerProduit(@PathVariable int id) {
		if (!productDao.existsById(id))
			throw new ProductNotFoundException("Le produit correspondant à l'id " + id + " n'existe pas");
		productDao.deleteById(id);
	}

	@Override
	public Health health() {
		System.out.println("****** Actuator : ProductController health() ");
		List<Product> products = productDao.findAll();
		if (products.isEmpty()) {
			return Health.down().build();
		}
		return Health.up().build();
	}
}
