package com.zyl.bean;

public class Cost {

    private Integer id;

    private Integer money;
    
    private String entCode;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

	public Cost(Integer money) {
		this.money = money;
	}

	public Cost() {
	}

	public String getEntCode() {
		return entCode;
	}

	public void setEntCode(String entCode) {
		this.entCode = entCode;
	}

	@Override
	public String toString() {
		return "Cost {id=" + id + ", money=" + money + ", entCode=" + entCode + "}";
	}
    
}