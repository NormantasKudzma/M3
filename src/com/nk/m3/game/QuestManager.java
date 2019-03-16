package com.nk.m3.game;

import java.util.ArrayList;

import com.ovl.graphics.SimpleFont;
import com.ovl.utils.OverloadRandom;
import com.ovl.utils.Vector2;

public class QuestManager implements Grid.MatchListener {
	private static final int firstQuestAt = 4; //4
	private static final int questDelayIncrement = 12; //12

	static interface CompletionListener {
		public void onQuestCompleted(Quest quest);
	}

	enum QuestType {
		MakeCombos("Get combos\n[%d] left\n(Reward +%d)"), MatchColors(
						"Destroy %s gems\n[%d] left\n(Reward +%d)"), MatchSizes(
						"Combine %d gems\n[%d] times\n(Reward +%d)"), MatchSizesInARow(
						"Combine %d gems\nin a row\n[%d] times\n(Reward +%d)");

		private QuestType(String textFormat) {
			format = textFormat;
		}

		public final String format;
	}

	private ArrayList<Quest> activeQuests;
	private Vector2 textOffsets[];
	private SimpleFont textObjects[];
	private CompletionListener listener;
	private MoveBatch<QuestMove> moveBatch;
	private QuestType lastType;
	private int chainsCompleted;
	private int nextQuestAt;
	private int questIndex;

	public QuestManager(SimpleFont textObjects[]) {
		this.textObjects = textObjects;
		activeQuests = new ArrayList<Quest>();

		textOffsets = new Vector2[textObjects.length];
		for (int i = 0; i < textObjects.length; ++i) {
			textOffsets[i] = textObjects[i].getPosition().copy();
		}

		nextQuestAt = firstQuestAt;
		moveBatch = new MoveBatch<QuestMove>();
	}

	public void setListener(CompletionListener listener) {
		this.listener = listener;
	}

	@Override
	public void onMatchChain(final Match match) {
		for (Quest q : activeQuests) {
			q.onMatchChain(match);
		}
	}

	@Override
	public void onMatchChainEnded() {
		for (Quest q : activeQuests) {
			q.onMatchChainEnded();
		}

		++chainsCompleted;
		if (chainsCompleted > nextQuestAt && activeQuests.size() < textObjects.length) {
			nextQuestAt = (int) (chainsCompleted * 1.1f) + questDelayIncrement;
			startNewQuest();
		}
	}

	private void startNewQuest() {
		QuestType[] allTypes = QuestType.values();
		QuestType type = null;
		do {
			type = allTypes[OverloadRandom.next(allTypes.length)];
		} while (type == lastType);
		lastType = type;

		SimpleFont textObj = textObjects[questIndex];
		textObj.setVisible(true);

		Quest quest = new Quest(textObj, listener, moveBatch, this);
		quest.type = type;
		quest.format = type.format;

		switch (type) {
			case MakeCombos: {
				quest.count = OverloadRandom.next(5) + 5;
				quest.reward = quest.count * 100;
				break;
			}
			case MatchColors: {
				quest.count = OverloadRandom.next(30) + 20;
				GemColor[] allColors = GemColor.values();
				quest.color = allColors[OverloadRandom.next(allColors.length)];
				quest.reward = quest.count * 15;
				break;
			}
			case MatchSizes: {
				quest.count = OverloadRandom.next(18) + 3;
				if (quest.count <= 5) {
					quest.comboSize = 5;
					quest.reward = quest.count * 220;
				}
				else
					if (quest.count <= 8) {
						quest.comboSize = 6;
						quest.reward = quest.count * 120;
					}
					else
						if (quest.count <= 12) {
							quest.comboSize = 4;
							quest.reward = quest.count * 85;
						}
						else {
							quest.comboSize = 3;
							quest.reward = quest.count * 25;
						}
				break;
			}
			case MatchSizesInARow: {
				quest.count = OverloadRandom.next(8) + 2;
				if (quest.count <= 2) {
					quest.comboSize = 5;
					quest.reward = quest.count * 400;
				}
				else
					if (quest.count <= 4) {
						quest.comboSize = 4;
						quest.reward = quest.count * 150;
					}
					else {
						quest.comboSize = 3;
						quest.reward = quest.count * 50;
					}
				break;
			}
		}

		Vector2 to = textOffsets[activeQuests.size()];
		Vector2 from = new Vector2(to.x, -1.0f);
		textObj.setPosition(from);
		moveBatch.addMove(new QuestMove(quest, from, to, QuestMove.in));

		quest.updateText();
		questIndex = (questIndex + 1) % textObjects.length;
		activeQuests.add(quest);
	}

	public void removeQuest(Quest quest) {
		activeQuests.remove(quest);
		moveActiveQuests();
	}

	private void moveActiveQuests() {
		for (int i = 0; i < activeQuests.size(); ++i) {
			Quest q = activeQuests.get(i);
			moveBatch.addMove(new QuestMove(q, q.getTextObj().getPosition(), textOffsets[i], QuestMove.move));
		}
	}

	public void update(float deltaTime) {
		moveBatch.update(deltaTime);
	}
}
