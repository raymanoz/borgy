import {History} from "history";
import React, {Component} from "react";
import Button from "react-bootstrap/Button";
import {connect} from "react-redux";
import {AppState} from "../../store";
import {excludeFromTrial, includeInTrial, startTrial, updateTrialName} from "../../store/newtrial/operations";
import {StartTrial} from "../../store/newtrial/types";
import {fetchScales} from "../../store/scales/operations";
import {Scale, Scales} from "../Scale";

interface Props {
    fetchScales: () => void;
    includeInTrial: (scale: Scale) => void;
    excludeFromTrial: (scale: Scale) => void;
    updateTrialName: (name: string) => void;
    startTrial: (trial: StartTrial, history: History) => void;

    history: History;
    scales: Scales;
    trialName: string;
    trialScales: Scales;
}

class NewTrial extends Component<Props> {
    constructor(props: Readonly<Props>) {
        super(props);
        this.renderScaleCheckbox = this.renderScaleCheckbox.bind(this);
        this.startTrial = this.startTrial.bind(this);
        this.handleScaleSelectionChange = this.handleScaleSelectionChange.bind(this);
    }

    public componentDidMount() {
        this.props.fetchScales();
    }

    public render() {
        return (<span id="newTrial">
                <h2>New trial</h2>
                <div className="field">
                    <label id="basic-addon1">Name</label>
                    <input type="text" placeholder="Name" aria-label="Name"
                             aria-describedby="basic-addon1" onChange={(event) => this.onTrialNameChanged(event.target.value)}/>
                </div>
                {this.props.scales.map((v) => this.renderScaleCheckbox(v))}
                <Button className="primary" onClick={this.startTrial}
                    disabled={this.props.trialName === "" || this.props.trialScales.length === 0}>Begin
                </Button>
            </span>);
    }

    private startTrial() {
        this.props.startTrial({
            name: this.props.trialName,
            scales: this.props.trialScales.map((s) => s.name),
        }, this.props.history);
    }

    private onTrialNameChanged(value: string) {
        this.props.updateTrialName(value);
    }

    private renderScaleCheckbox(scale: Scale) {
        return <div className="field checkbox" key={scale.name}>
            <label htmlFor={scale.name}>
                <input type="checkbox" id={scale.name}
                       onChange={this.handleScaleSelectionChange}/>
                <span>{scale.name}</span>
            </label>
        </div>;
    }

    private handleScaleSelectionChange(event: React.ChangeEvent<HTMLInputElement>) {
        const changingScale = this.props.scales.find((scale) => scale.name === event.target.id);
        if (changingScale) {
            if (this.props.trialScales.find((scale) => scale.name === changingScale.name)) {
                this.props.excludeFromTrial(changingScale);
            } else {
                this.props.includeInTrial(changingScale);
            }
        }
    }
}

const mapStateToProps = (state: AppState) => ({
    scales: state.scales,
    trialName: state.newTrial.trialName,
    trialScales: state.newTrial.scales,
});

export default connect(
    mapStateToProps,
    {fetchScales, includeInTrial, excludeFromTrial, updateTrialName, startTrial},
)(NewTrial);
