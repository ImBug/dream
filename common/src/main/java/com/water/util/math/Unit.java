package com.water.util.math;

public class Unit {

	public static final Unit[] ALL_pps = new Unit[4];
	public static final Unit pps = new Unit(ALL_pps, 1L, "pps");
	public static final Unit kpps = new Unit(ALL_pps, 1000L, "kpps");
	public static final Unit Mpps = new Unit(ALL_pps, 1000000L, "Mpps");
	public static final Unit Gpps = new Unit(ALL_pps, 1000000000L, "Gpps");

	public static final Unit[] ALL_B = new Unit[4];
	public static final Unit B = new Unit(ALL_B, 1L, "B");
	public static final Unit KB = new Unit(ALL_B, 1024L, "KB");
	public static final Unit MB = new Unit(ALL_B, 1048576L, "MB");
	public static final Unit GB = new Unit(ALL_B, 1073741824L, "GB");

	public static final Unit[] ALL_ms = new Unit[5];
	public static final Unit ms = new Unit(ALL_ms, 1L, "毫秒");
	public static final Unit second = new Unit(ALL_ms, 1000L, "秒");
	public static final Unit minute = new Unit(ALL_ms, 60000L, "分");
	public static final Unit hour = new Unit(ALL_ms, 3600000L, "小时");
	public static final Unit day = new Unit(ALL_ms, 86400000L, "天");

	public static final Unit[] ALL_percent = new Unit[1];
	public static final Unit percent = new Unit(ALL_percent, 1L, "%");

	public static final Unit[][] ALL = { ALL_pps, ALL_B, ALL_percent, ALL_ms };
	private Unit[] all;

	static {

		ALL_pps[0] = pps;
		ALL_pps[1] = kpps;
		ALL_pps[2] = Mpps;
		ALL_pps[3] = Gpps;

		ALL_B[0] = B;
		ALL_B[1] = KB;
		ALL_B[2] = MB;
		ALL_B[3] = GB;

		ALL_ms[0] = ms;
		ALL_ms[1] = second;
		ALL_ms[2] = minute;
		ALL_ms[3] = hour;
		ALL_ms[4] = day;

		ALL_percent[0] = percent;
	}

	private long radix;

	private String code;
	private String name;

	public Unit(Unit[] all, long radix, String code) {
		this.all = all;
		this.radix = radix;
		this.code = code;
		name = code;
	}

	public Unit(Unit[] all, long radix, String code, String name) {
		this.all = all;
		this.radix = radix;
		this.code = code;
		this.name = name;
	}

	public Unit[] getAll() {
		return all;
	}

	public long getRadix() {
		return radix;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public double to(Unit to, double value) {
		if (this == to)
			return value;
		if (all != all) {
			throw new IllegalArgumentException(String.format("单位转换失败，当前单位[%s]不允许转换为目标单位[%s]", new Object[] { this, to }));
		}
		return value / (to.getRadix() * 1.0D / getRadix());
	}

	public String formatPrefer(double value) {
		for (int i = all.length - 1; i > 0; i--) {
			double temp = to(all[i], value);
			if (temp >= 1.0D)
				return all[i].format(temp, true);
		}
		double temp = to(all[0], value);
		return all[0].format(temp, true);
	}

	public String format(double value) {
		return format(value, true);
	}

	public String format(double value, boolean appendUnit) {
		if (appendUnit) {
			if (value == value) {
				return new Double(value).longValue() + code;
			}
			return Double.toString((value * 10.0D) / 10.0D) + code;
		}
		if (value == value) {
			return new Double(value).longValue() + "";
		}
		return Double.toString((value * 10.0D) / 10.0D);
	}

	public Unit getUnitPrefer(double value) {
		for (int i = all.length - 1; i > 0; i--) {
			double temp = to(all[i], value);
			if (temp >= 1.0D)
				return all[i];
		}
		return all[0];
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Unit))
			return false;
		Unit other = (Unit) obj;
		return code.equals(code);
	}

	public String toString() {
		return name;
	}
}
