import pandas as pd
import random
import os
import shutil
from datetime import datetime, timedelta

# Define the target directory
output_dir = "../backend/data"
os.makedirs(output_dir, exist_ok=True)

stores = ["profi", "kaufland", "lidl"]

def process_weekly_data(store, selected_dataset, current_date, is_forward):
    """Generates weekly price lists with all prices set to 10.0 and discounts."""
    # Set all prices to 10.0
    selected_dataset["price"] = 10.0

    # Save the price list in the target directory
    pricelist_path = f"{output_dir}/{store}_{current_date.strftime('%Y-%m-%d')}.csv"
    selected_dataset.to_csv(pricelist_path, sep=";", index=False)

    # Generate discount data
    week_discounts = selected_dataset.sample(n=random.randint(10, 12))
    discount_data = pd.DataFrame({
        "product_id": week_discounts["product_id"],
        "product_name": week_discounts["product_name"],
        "brand": week_discounts["brand"],
        "package_quantity": week_discounts["package_quantity"],
        "package_unit": week_discounts["package_unit"],
        "product_category": week_discounts["product_category"],
        "from_date": current_date.strftime("%Y-%m-%d"),
        "to_date": (current_date + timedelta(days=random.randint(3, 7) - 1)).strftime("%Y-%m-%d"),
        "percentage_of_discount": [random.randint(5, 30) for _ in range(len(week_discounts))]
    })
    discount_path = f"{output_dir}/{store}_discounts_{current_date.strftime('%Y-%m-%d')}.csv"
    discount_data.to_csv(discount_path, sep=";", index=False)

for store in stores:
    try:
        # Load datasets and set all prices to 10.0
        dataset1 = pd.read_csv(f"../input_files/{store}_2025-05-01.csv", sep=";")
        dataset1["price"] = 10.0
        dataset2 = pd.read_csv(f"../input_files/{store}_2025-05-08.csv", sep=";")
        dataset2["price"] = 10.0
    except FileNotFoundError:
        print(f"Missing files for {store}. Skipping...")
        continue

    # Save modified price list files to the target directory
    dataset1.to_csv(f"{output_dir}/{store}_2025-05-01.csv", sep=";", index=False)
    dataset2.to_csv(f"{output_dir}/{store}_2025-05-08.csv", sep=";", index=False)

    # Copy original discounts files if they exist (without modifying prices)
    discounts_file1 = f"../input_files/{store}_discounts_2025-05-01.csv"
    discounts_file2 = f"../input_files/{store}_discounts_2025-05-08.csv"
    if os.path.exists(discounts_file1):
        shutil.copyfile(discounts_file1, f"{output_dir}/{store}_discounts_2025-05-01.csv")
    if os.path.exists(discounts_file2):
        shutil.copyfile(discounts_file2, f"{output_dir}/{store}_discounts_2025-05-08.csv")

    # Generate past data for 16 weeks
    backward_base = datetime.strptime("2025-05-08", "%Y-%m-%d")
    for i in range(1, 17):
        current_date = backward_base - timedelta(weeks=i)
        selected_dataset = dataset1.copy() if i % 2 == 1 else dataset2.copy()
        process_weekly_data(store, selected_dataset, current_date, is_forward=False)

    # Generate future data for 4 weeks
    forward_base = datetime.strptime("2025-05-08", "%Y-%m-%d")
    for i in range(1, 5):
        current_date = forward_base + timedelta(weeks=i)
        selected_dataset = dataset1.copy() if i % 2 == 1 else dataset2.copy()
        process_weekly_data(store, selected_dataset, current_date, is_forward=True)
