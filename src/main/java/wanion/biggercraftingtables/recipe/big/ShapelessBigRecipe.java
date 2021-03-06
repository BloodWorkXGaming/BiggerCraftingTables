package wanion.biggercraftingtables.recipe.big;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraft.item.ItemStack;
import wanion.biggercraftingtables.recipe.AbstractShapelessAdvancedRecipe;

import javax.annotation.Nonnull;

public final class ShapelessBigRecipe extends AbstractShapelessAdvancedRecipe implements BigRecipeRegistry.IBigRecipe
{
	public ShapelessBigRecipe(@Nonnull ItemStack output, @Nonnull Object... inputs)
	{
		super(output, inputs);
	}
}