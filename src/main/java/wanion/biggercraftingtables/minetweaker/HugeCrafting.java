package wanion.biggercraftingtables.minetweaker;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import wanion.biggercraftingtables.recipe.huge.HugeRecipeRegistry;
import wanion.biggercraftingtables.recipe.huge.IHugeRecipe;
import wanion.biggercraftingtables.recipe.huge.ShapedHugeRecipe;
import wanion.biggercraftingtables.recipe.huge.ShapelessHugeRecipe;

import javax.annotation.Nonnull;
import java.util.List;

@ZenClass("mods.biggercraftingtables.Huge")
public final class HugeCrafting
{
	private HugeCrafting() {}

	@ZenMethod
	public static void addShaped(@Nonnull final IItemStack output, @Nonnull final IIngredient[][] inputs)
	{
		int height = inputs.length;
		int width = 0;
		for (final IIngredient[] row : inputs)
			if (width < row.length)
				width = row.length;
		final Object[] input = new Object[width * height];
		int x = 0;
		for (final IIngredient[] row : inputs)
			for (IIngredient ingredient : row)
				input[x++] = Tweaker.toActualObject(ingredient);
		MineTweakerAPI.apply(new Add(new ShapedHugeRecipe(Tweaker.toStack(output), input, width, height)));
	}

	@ZenMethod
	public static void addShapeless(@Nonnull final IItemStack output, @Nonnull final IIngredient[] inputs)
	{
		MineTweakerAPI.apply(new Add(new ShapelessHugeRecipe(Tweaker.toStack(output), Tweaker.toObjects(inputs))));
	}

	@ZenMethod
	public static void remove(final IItemStack target)
	{
		MineTweakerAPI.apply(new Remove(Tweaker.toStack(target)));
	}

	private static class Add implements IUndoableAction
	{
		private final IHugeRecipe recipe;

		public Add(@Nonnull final IHugeRecipe recipe)
		{
			this.recipe = recipe;
		}

		@Override
		public void apply()
		{
			HugeRecipeRegistry.instance.addRecipe(recipe);
		}

		@Override
		public boolean canUndo()
		{
			return true;
		}

		@Override
		public void undo()
		{
			HugeRecipeRegistry.instance.removeRecipe(recipe);
		}

		@Override
		public String describe()
		{
			return "Adding HugeRecipe for " + recipe.getOutput().getDisplayName();
		}

		@Override
		public String describeUndo()
		{
			return "Un-adding HugeRecipe Recipe for " + recipe.getOutput().getDisplayName();
		}

		@Override
		public Object getOverrideKey()
		{
			return null;
		}
	}

	private static class Remove implements IUndoableAction
	{
		private final ItemStack itemStackToRemove;
		private final IHugeRecipe recipe;

		private Remove(@Nonnull final ItemStack itemStackToRemove)
		{
			this.itemStackToRemove = itemStackToRemove;
			IHugeRecipe recipe = null;
			for (final List<IHugeRecipe> hugeRecipeList : HugeRecipeRegistry.instance.shapedRecipes.valueCollection()) {
				if (hugeRecipeList == null)
					continue;
				for (final IHugeRecipe hugeRecipe : hugeRecipeList) {
					if (hugeRecipe.getOutput().isItemEqual(itemStackToRemove)) {
						recipe = hugeRecipe;
						HugeRecipeRegistry.instance.removeRecipe(recipe);
						break;
					}
				}
			}
			if (recipe == null) {
				for (final List<IHugeRecipe> hugeRecipeList : HugeRecipeRegistry.instance.shapelessRecipes.valueCollection()) {
					if (hugeRecipeList == null)
						continue;
					for (final IHugeRecipe hugeRecipe : hugeRecipeList) {
						if (hugeRecipe.getOutput().isItemEqual(itemStackToRemove)) {
							recipe = hugeRecipe;
							HugeRecipeRegistry.instance.removeRecipe(recipe);
							break;
						}
					}
				}
			}
			this.recipe = recipe;
		}

		@Override
		public void apply()
		{
			HugeRecipeRegistry.instance.removeRecipe(recipe);
		}

		@Override
		public boolean canUndo()
		{
			return recipe != null;
		}

		@Override
		public void undo()
		{
			HugeRecipeRegistry.instance.addRecipe(recipe);
		}

		@Override
		public String describe()
		{
			return "Removing HugeRecipe for " + itemStackToRemove.getDisplayName();
		}

		@Override
		public String describeUndo()
		{
			return "Un-removing HugeRecipe for " + itemStackToRemove.getDisplayName();
		}

		@Override
		public Object getOverrideKey()
		{
			return null;
		}
	}
}