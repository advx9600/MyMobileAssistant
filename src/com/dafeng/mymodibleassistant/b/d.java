package com.dafeng.mymodibleassistant.b;

public class d {
	private String pkg;
	private String name;

	public d() {

	}

	public d(String pkg, String name) {
		setPkg(pkg);
		setName(name);
	}

	public boolean isEqual(d d) {
		if (isEqual(d.getPkg(), d.getName())) {
			return true;
		}
		return false;
	}

	public boolean isEqual(String pkg, String name) {
		if (pkg != null && name != null && pkg.length() > 0
				&& name.length() > 0) {
			if (pkg.equals(getPkg()) && name.equals(getName())) {
				return true;
			}
		}
		return false;
	}

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
