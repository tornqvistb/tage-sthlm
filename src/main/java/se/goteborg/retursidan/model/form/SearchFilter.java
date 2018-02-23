package se.goteborg.retursidan.model.form;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import se.goteborg.retursidan.model.GeneralModelBean;
import se.goteborg.retursidan.model.entity.Advertisement.Status;
import se.goteborg.retursidan.model.entity.Category;
import se.goteborg.retursidan.model.entity.Unit;

/**
 * Model bean representing a search filter
 *
 */
@Component
public class SearchFilter extends GeneralModelBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Category topCategory;
	private Category subCategory;
	private Unit unit;
	private Status status;
	private String searchString;

	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Category getTopCategory() {
		return topCategory;
	}
	public void setTopCategory(Category topCategory) {
		this.topCategory = topCategory;
	}
	public Category getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(Category subCategory) {
		this.subCategory = subCategory;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
}
